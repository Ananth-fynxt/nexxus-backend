package nexxus.auth.service;

import org.springframework.http.ResponseEntity;

import nexxus.auth.dto.TokenResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface AuthService extends BaseService {

  Mono<ResponseEntity<TokenResponse>> exchangeCodeForToken(
      String code, String redirectUri, String state);
}
