package nexxus.environment.service;

import org.springframework.http.ResponseEntity;

import nexxus.environment.dto.EnvironmentDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface EnvironmentService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(EnvironmentDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> readAll();

  Mono<ResponseEntity<ApiResponse<Object>>> read(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, EnvironmentDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> findByBrandId(String brandId);

  Mono<ResponseEntity<ApiResponse<Object>>> rotateSecret(String id);
}
