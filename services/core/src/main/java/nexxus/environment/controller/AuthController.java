package nexxus.environment.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nexxus.shared.auth.AuthUrlService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthUrlService authUrlService;

  @GetMapping("/login")
  public Mono<ResponseEntity<Void>> login(
      @RequestParam("redirectUri") String redirectUri,
      @RequestParam(value = "state", required = false) String state) {
    String loginUrl = authUrlService.buildLoginUrl(redirectUri, state);
    return Mono.just(ResponseEntity.status(302).location(URI.create(loginUrl)).build());
  }

  @GetMapping("/logout")
  public Mono<ResponseEntity<Void>> logout(@RequestParam("returnTo") String returnTo) {
    String logoutUrl = authUrlService.buildLogoutUrl(returnTo);
    return Mono.just(ResponseEntity.status(302).location(URI.create(logoutUrl)).build());
  }

  @GetMapping("/callback")
  public Mono<ResponseEntity<String>> callback() {
    // Callback endpoint - token exchange should happen on the client or via a dedicated endpoint
    return Mono.just(
        ResponseEntity.ok("Login callback received. Exchange the code on client-side."));
  }
}
