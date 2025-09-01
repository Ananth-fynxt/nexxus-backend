package nexxus.brand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.brand.dto.BrandDto;
import nexxus.brand.service.BrandService;
import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController extends BaseController {

  private final BrandService brandService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @Validated @RequestBody BrandDto brandDto) {
    return brandService.create(brandDto);
  }

  @GetMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return brandService.readAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> read(@PathVariable("id") String id) {
    return brandService.read(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @Validated @RequestBody BrandDto brandDto) {
    return brandService.update(id, brandDto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return brandService.delete(id);
  }
}
