package nexxus.auth.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import nexxus.auth.dto.TokenResponse;
import nexxus.auth.service.AuthService;
import nexxus.shared.config.Auth0Properties;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final Auth0Properties auth0Properties;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  private WebClient createWebClient() {
    return WebClient.builder().baseUrl("https://" + auth0Properties.getDomain()).build();
  }

  @Override
  public Mono<ResponseEntity<TokenResponse>> exchangeCodeForToken(
      String code, String redirectUri, String state) {
    WebClient webClient = createWebClient();

    // Auth0 expects application/x-www-form-urlencoded for token exchange
    return webClient
        .post()
        .uri("/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(
            BodyInserters.fromFormData("grant_type", "authorization_code")
                .with("client_id", auth0Properties.getClientId())
                .with("client_secret", auth0Properties.getClientSecret())
                .with("code", code)
                .with("redirect_uri", redirectUri))
        .retrieve()
        .bodyToMono(TokenResponse.class)
        .map(ResponseEntity::ok)
        .onErrorResume(
            WebClientResponseException.class,
            ex ->
                Mono.error(
                    new IllegalStateException(
                        "Auth0 token exchange failed: " + ex.getResponseBodyAsString(), ex)));
  }
}
