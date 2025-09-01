package nexxus.environment.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nexxus.shared.auth.AuthUrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth0 login/logout helpers and callback endpoint")
public class AuthController {

  private final AuthUrlService authUrlService;

  @GetMapping("/login")
  @Operation(summary = "Start Auth0 login", description = "Redirects to Auth0 authorize endpoint")
  public Mono<ResponseEntity<Void>> login(
      @Parameter(description = "Redirect URI configured in Auth0", required = true)
          @RequestParam(value = "redirectUri")
          String redirectUri,
      @Parameter(description = "Opaque state value to maintain state between request and callback")
          @RequestParam(value = "state", required = false)
          String state) {
    try {
      if (redirectUri == null || redirectUri.isBlank()) {
        return Mono.just(ResponseEntity.badRequest().build());
      }
      String loginUrl = authUrlService.buildLoginUrl(redirectUri, state);
      return Mono.just(ResponseEntity.status(302).location(URI.create(loginUrl)).build());
    } catch (Exception e) {
      // Return a user-friendly error response instead of throwing exception
      return Mono.just(ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/logout")
  @Operation(summary = "Start Auth0 logout", description = "Redirects to Auth0 logout endpoint")
  public Mono<ResponseEntity<Void>> logout(
      @Parameter(description = "URL to return to after logout", required = true)
          @RequestParam(value = "returnTo")
          String returnTo) {
    try {
      if (returnTo == null || returnTo.isBlank()) {
        return Mono.just(ResponseEntity.badRequest().build());
      }
      String logoutUrl = authUrlService.buildLogoutUrl(returnTo);
      return Mono.just(ResponseEntity.status(302).location(URI.create(logoutUrl)).build());
    } catch (Exception e) {
      // Return a user-friendly error response instead of throwing exception
      return Mono.just(ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/callback")
  @Operation(summary = "Auth0 callback", description = "Handles Auth0 redirect after login")
  public Mono<ResponseEntity<String>> callback(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription) {

    if (error != null) {
      return Mono.just(
          ResponseEntity.badRequest().body("Authentication failed: " + errorDescription));
    }

    if (code != null) {
      // In a real implementation, you would exchange this code for tokens
      return Mono.just(
          ResponseEntity.ok()
              .body("Authentication successful! Code received: " + code.substring(0, 10) + "..."));
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
