package nexxus.riskrule.service;

import org.springframework.http.ResponseEntity;

import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface RiskRuleService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(RiskRuleDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> findById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> findAllByBrandIdAndEnvironmentId(
      String brandId, String environmentId);

  Mono<ResponseEntity<ApiResponse<Object>>> findAllByPspId(String pspId);

  Mono<ResponseEntity<ApiResponse<Object>>> deleteById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, RiskRuleDto dto);
}
