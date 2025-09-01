package nexxus.fee.service;

import org.springframework.http.ResponseEntity;

import nexxus.fee.dto.FeeDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface FeeService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(FeeDto feeDto);

  Mono<ResponseEntity<ApiResponse<Object>>> getById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      String brandId, String environmentId);

  Mono<ResponseEntity<ApiResponse<Object>>> getByPspId(String pspId);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FeeDto feeDto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);
}
