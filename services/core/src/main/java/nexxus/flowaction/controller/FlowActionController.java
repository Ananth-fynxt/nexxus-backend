package nexxus.flowaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowaction.dto.FlowActionDto;
import nexxus.flowaction.service.FlowActionService;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/flow-types/{flowTypeId}/flow-actions")
@RequiredArgsConstructor
public class FlowActionController {

  private final FlowActionService flowActionService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @PathVariable("flowTypeId") String flowTypeId, @Validated @RequestBody FlowActionDto dto) {
    dto.setFlowTypeId(flowTypeId);
    return flowActionService.create(dto);
  }

  @GetMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll(
      @PathVariable("flowTypeId") String flowTypeId) {
    return flowActionService.findByFlowTypeId(flowTypeId);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> read(@PathVariable("id") String id) {
    return flowActionService.read(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable("id") String id, @Validated @RequestBody FlowActionDto dto) {
    return flowActionService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return flowActionService.delete(id);
  }

  @GetMapping("/name/{name}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findByNameAndFlowTypeId(
      @PathVariable("name") String name, @PathVariable("flowTypeId") String flowTypeId) {
    return flowActionService.findByNameAndFlowTypeId(name, flowTypeId);
  }
}
