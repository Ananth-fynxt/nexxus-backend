package nexxus.flowaction.service;

import org.springframework.http.ResponseEntity;

import nexxus.flowaction.dto.FlowActionDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface FlowActionService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(FlowActionDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> readAll();

  Mono<ResponseEntity<ApiResponse<Object>>> read(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FlowActionDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> findByFlowTypeId(String flowTypeId);

  Mono<ResponseEntity<ApiResponse<Object>>> findByNameAndFlowTypeId(String name, String flowTypeId);
}
