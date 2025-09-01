package nexxus.flowtarget.service;

import org.springframework.http.ResponseEntity;

import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.flowtarget.entity.FlowTarget;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface FlowTargetService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(String flowTypeId, FlowTargetDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> readAll(String flowTypeId);

  Mono<ResponseEntity<ApiResponse<Object>>> read(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String flowTypeId, String id, FlowTargetDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);

  Mono<FlowTarget> getFlowTargetById(String id);
}
