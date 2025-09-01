package nexxus.conversionrate.service;

import org.springframework.http.ResponseEntity;

import nexxus.conversionrate.dto.ConversionRateDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface ConversionRateService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(ConversionRateDto conversionRateDto);

  Mono<ResponseEntity<ApiResponse<Object>>> findById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> findByBrandAndEnvironmentId(
      String brandId, String environmentId);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, ConversionRateDto conversionRateDto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);
}
