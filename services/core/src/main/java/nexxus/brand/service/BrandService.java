package nexxus.brand.service;

import org.springframework.http.ResponseEntity;

import nexxus.brand.dto.BrandDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface BrandService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(BrandDto brandDto);

  Mono<ResponseEntity<ApiResponse<Object>>> readAll();

  Mono<ResponseEntity<ApiResponse<Object>>> read(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, BrandDto brandDto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);
}
