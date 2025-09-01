package nexxus.auth.service;

import org.springframework.http.ResponseEntity;

import nexxus.auth.dto.TokenResponse;

import reactor.core.publisher.Mono;

public interface AuthService {

  Mono<ResponseEntity<TokenResponse>> exchangeCodeForToken(
      String code, String redirectUri, String state);
}
