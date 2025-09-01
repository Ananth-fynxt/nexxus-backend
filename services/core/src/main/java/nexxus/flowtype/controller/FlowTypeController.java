package nexxus.flowtype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowtype.dto.FlowTypeDto;
import nexxus.flowtype.service.FlowTypeService;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/flow-types")
@RequiredArgsConstructor
public class FlowTypeController {

  private final FlowTypeService flowTypeService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(@Validated @RequestBody FlowTypeDto dto) {
    return flowTypeService.create(dto);
  }

  @GetMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return flowTypeService.readAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> read(@PathVariable("id") String id) {
    return flowTypeService.read(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @Validated @RequestBody FlowTypeDto dto) {
    return flowTypeService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return flowTypeService.delete(id);
  }

  @GetMapping("/name/{name}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findByName(@PathVariable String name) {
    return flowTypeService.findByName(name);
  }
}
