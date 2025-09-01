package nexxus.flowtype.service;

import org.springframework.http.ResponseEntity;

import nexxus.flowtype.dto.FlowTypeDto;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;

import reactor.core.publisher.Mono;

public interface FlowTypeService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(FlowTypeDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> readAll();

  Mono<ResponseEntity<ApiResponse<Object>>> read(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FlowTypeDto dto);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> findByName(String name);
}
