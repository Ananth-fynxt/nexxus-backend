package nexxus.environment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.environment.dto.EnvironmentDto;
import nexxus.environment.service.EnvironmentService;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/environments")
@RequiredArgsConstructor
public class EnvironmentController {

  private final EnvironmentService environmentService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @Validated @RequestBody EnvironmentDto dto) {
    return environmentService.create(dto);
  }

  @GetMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return environmentService.readAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> read(@PathVariable("id") String id) {
    return environmentService.read(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable("id") String id, @Validated @RequestBody EnvironmentDto dto) {
    return environmentService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return environmentService.delete(id);
  }

  @GetMapping("/brand/{brandId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findByBrandId(
      @PathVariable("brandId") String brandId) {
    return environmentService.findByBrandId(brandId);
  }

  @PutMapping("/{id}/rotate-secret")
  public Mono<ResponseEntity<ApiResponse<Object>>> rotateSecret(@PathVariable("id") String id) {
    return environmentService.rotateSecret(id);
  }
}
