package nexxus.flowtarget.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.flowtarget.service.FlowTargetService;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/flow-types/{flowTypeId}/flow-targets")
@RequiredArgsConstructor
public class FlowTargetController {

  private final FlowTargetService flowTargetService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @PathVariable("flowTypeId") String flowTypeId, @Validated @RequestBody FlowTargetDto dto) {
    dto.setFlowTypeId(flowTypeId);
    return flowTargetService.create(flowTypeId, dto);
  }

  @GetMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll(
      @PathVariable("flowTypeId") String flowTypeId) {
    return flowTargetService.readAll(flowTypeId);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> read(@PathVariable("id") String id) {
    return flowTargetService.read(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable("flowTypeId") String flowTypeId,
      @PathVariable("id") String id,
      @Validated @RequestBody FlowTargetDto dto) {
    return flowTargetService.update(flowTypeId, id, dto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return flowTargetService.delete(id);
  }
}
