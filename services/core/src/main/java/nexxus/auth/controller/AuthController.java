package nexxus.auth.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nexxus.auth.service.AuthService;
import nexxus.shared.auth.AuthUrlService;
import nexxus.shared.config.Auth0Properties;
import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController extends BaseController {

  private final AuthUrlService authUrlService;
  private final AuthService authService;
  private final Auth0Properties auth0Properties;

  @GetMapping("/login")
  public Mono<ResponseEntity<Void>> login(
      @RequestParam(value = "redirectUri", required = false) String redirectUri,
      @RequestParam(value = "state", required = false) String state) {
    try {
      String finalRedirectUri =
          redirectUri != null ? redirectUri : auth0Properties.getCallbackUrl();
      String loginUrl = authUrlService.buildLoginUrl(finalRedirectUri, state);
      return Mono.just(ResponseEntity.status(302).location(URI.create(loginUrl)).build());
    } catch (Exception e) {
      return Mono.just(ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/logout")
  public Mono<ResponseEntity<Void>> logout(
      @RequestParam(value = "returnTo", required = false) String returnTo) {
    try {
      String finalReturnTo = returnTo != null ? returnTo : auth0Properties.getFrontendUrl();
      String logoutUrl = authUrlService.buildLogoutUrl(finalReturnTo);
      return Mono.just(ResponseEntity.status(302).location(URI.create(logoutUrl)).build());
    } catch (Exception e) {
      return Mono.just(ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/callback")
  public Mono<ResponseEntity<ApiResponse<Object>>> callback(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription) {

    if (error != null) {
      return authService.customError(
          nexxus.shared.constants.ErrorCode.AUTHENTICATION_FAILED,
          "Authentication failed: " + (errorDescription != null ? errorDescription : error),
          org.springframework.http.HttpStatus.UNAUTHORIZED);
    }

    if (code != null) {
      String redirectUri = auth0Properties.getCallbackUrl();
      return authService.exchangeCodeForToken(code, redirectUri, state);
    }

    return authService.successResponse(
        "Auth0 callback endpoint. This should be handled by your frontend application.");
  }

  @GetMapping("/config")
  public Mono<ResponseEntity<ApiResponse<Object>>> config() {
    try {
      String configInfo =
          String.format(
              "Auth0 Configured: %s, Domain: %s",
              authUrlService.isConfigured() ? "YES" : "NO", authUrlService.getDomain());
      return authService.successResponse(
          configInfo, "Auth0 configuration status retrieved successfully");
    } catch (Exception e) {
      return authService.customError(
          nexxus.shared.constants.ErrorCode.GENERIC_ERROR,
          "Auth0 configuration error: " + e.getMessage(),
          org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
