package nexxus.auth.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nexxus.auth.dto.TokenResponse;
import nexxus.auth.service.AuthService;
import nexxus.shared.auth.AuthUrlService;
import nexxus.shared.controller.BaseController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

  private final AuthUrlService authUrlService;
  private final AuthService authService;

  @GetMapping("/login")
  public Mono<ResponseEntity<Void>> login(
      @RequestParam(
              value = "redirectUri",
              required = false,
              defaultValue = "https://ca9a457eb330.ngrok-free.app/api/v1/auth/callback")
          String redirectUri,
      @RequestParam(value = "state", required = false) String state) {
    try {
      String loginUrl = authUrlService.buildLoginUrl(redirectUri, state);
      return Mono.just(ResponseEntity.status(302).location(URI.create(loginUrl)).build());
    } catch (Exception e) {
      // Return a user-friendly error response instead of throwing exception
      return Mono.just(ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/logout")
  public Mono<ResponseEntity<Void>> logout(
      @RequestParam(
              value = "returnTo",
              required = false,
              defaultValue = "https://ca9a457eb330.ngrok-free.app/api/v1/")
          String returnTo) {
    try {
      String logoutUrl = authUrlService.buildLogoutUrl(returnTo);
      return Mono.just(ResponseEntity.status(302).location(URI.create(logoutUrl)).build());
    } catch (Exception e) {
      // Return a user-friendly error response instead of throwing exception
      return Mono.just(ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/callback")
  public Mono<ResponseEntity<?>> callback(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription) {

    if (error != null) {
      return Mono.just(
          ResponseEntity.badRequest().body("Authentication failed: " + errorDescription));
    }

    if (code != null) {
      String redirectUri = "https://ca9a457eb330.ngrok-free.app/api/v1/auth/callback";
      return authService
          .exchangeCodeForToken(code, redirectUri, state)
          .map(response -> (ResponseEntity<?>) response);
    }

    return Mono.just(
        ResponseEntity.ok()
            .body("Auth0 callback endpoint. This should be handled by your frontend application."));
  }

  @GetMapping("/config")
  public Mono<ResponseEntity<String>> config() {
    try {
      // Simple endpoint to test if Auth0 properties are loaded
      String config =
          String.format(
              "Auth0 Configured: %s, Domain: %s",
              authUrlService.isConfigured() ? "YES" : "NO", authUrlService.getDomain());
      return Mono.just(ResponseEntity.ok(config));
    } catch (Exception e) {
      return Mono.just(ResponseEntity.ok("Auth0 config error: " + e.getMessage()));
    }
  }
}
