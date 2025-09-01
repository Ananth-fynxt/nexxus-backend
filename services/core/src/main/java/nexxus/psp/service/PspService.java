package nexxus.psp.service;

import org.springframework.http.ResponseEntity;

import nexxus.psp.dto.PspDto;
import nexxus.psp.dto.UpdatePspDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface PspService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(PspDto pspDto);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String pspId, UpdatePspDto pspDto);

  Mono<ResponseEntity<ApiResponse<Object>>> getById(String pspId);

  Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      String brandId, String environmentId);

  Mono<ResponseEntity<ApiResponse<Object>>>
      getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
          String brandId,
          String environmentId,
          String status,
          String currency,
          String flowActionId);

  Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironmentByStatusAndFlowAction(
      String brandId, String environmentId, String status, String flowActionId);

  Mono<ResponseEntity<ApiResponse<Object>>> getSupportedCurrenciesByBrandAndEnvironment(
      String brandId, String environmentId);
}
