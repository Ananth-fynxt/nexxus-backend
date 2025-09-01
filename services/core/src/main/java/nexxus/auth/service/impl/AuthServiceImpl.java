package nexxus.auth.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import nexxus.auth.dto.TokenResponse;
import nexxus.auth.service.AuthService;
import nexxus.shared.config.Auth0Properties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final Auth0Properties auth0Properties;

  private WebClient createWebClient() {
    return WebClient.builder().baseUrl("https://" + auth0Properties.getDomain()).build();
  }

  @Override
  public Mono<ResponseEntity<TokenResponse>> exchangeCodeForToken(
      String code, String redirectUri, String state) {
    WebClient webClient = createWebClient();

    Map<String, Object> payload = new HashMap<>();
    payload.put("grant_type", "authorization_code");
    payload.put("client_id", auth0Properties.getClientId());
    payload.put("client_secret", auth0Properties.getClientSecret());
    payload.put("code", code);
    payload.put("redirect_uri", redirectUri);

    return webClient
        .post()
        .uri("/oauth/token")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(payload))
        .retrieve()
        .bodyToMono(TokenResponse.class)
        .map(ResponseEntity::ok);
  }
}
