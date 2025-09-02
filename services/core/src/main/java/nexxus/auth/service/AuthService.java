package nexxus.auth.service;

import org.springframework.http.ResponseEntity;

import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface AuthService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> exchangeCodeForToken(
      String code, String redirectUri, String state);
}
