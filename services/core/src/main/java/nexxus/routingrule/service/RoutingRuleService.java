package nexxus.routingrule.service;

import org.springframework.http.ResponseEntity;

import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.routingrule.dto.UpdateRoutingRuleDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface RoutingRuleService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(RoutingRuleDto routingRuleDto);

  Mono<ResponseEntity<ApiResponse<Object>>> update(
      String id, UpdateRoutingRuleDto updateRoutingRuleDto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> getById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> readAllByBrandAndEnvironment(
      String brandId, String environmentId);
}
