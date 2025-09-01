package nexxus.flowdefinition.service;

import org.springframework.http.ResponseEntity;

import nexxus.flowdefinition.dto.FlowDefinitionDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface FlowDefinitionService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(FlowDefinitionDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> readAll();

  Mono<ResponseEntity<ApiResponse<Object>>> readAllByFlowTargetId(String flowTargetId);

  Mono<ResponseEntity<ApiResponse<Object>>> read(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FlowDefinitionDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> readAllByBrand();
}
