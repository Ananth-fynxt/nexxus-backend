package nexxus.flowdefinition.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowdefinition.dto.FlowDefinitionDto;
import nexxus.flowdefinition.service.FlowDefinitionService;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/flow-definitions")
@RequiredArgsConstructor
public class FlowDefinitionController {

  private final FlowDefinitionService flowDefinitionService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @Validated @RequestBody FlowDefinitionDto dto) {
    return flowDefinitionService.create(dto);
  }

  @GetMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return flowDefinitionService.readAll();
  }

  @GetMapping("/flow-target/{flowTargetId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> readAllByFlowTargetId(
      @PathVariable("flowTargetId") String flowTargetId) {
    return flowDefinitionService.readAllByFlowTargetId(flowTargetId);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> read(@PathVariable("id") String id) {
    return flowDefinitionService.read(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable("id") String id, @Validated @RequestBody FlowDefinitionDto dto) {
    return flowDefinitionService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return flowDefinitionService.delete(id);
  }

  @GetMapping("/brand")
  public Mono<ResponseEntity<ApiResponse<Object>>> readAllByBrand() {
    return flowDefinitionService.readAllByBrand();
  }
}
