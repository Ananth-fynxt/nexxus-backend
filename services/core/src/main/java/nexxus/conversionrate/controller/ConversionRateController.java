package nexxus.conversionrate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nexxus.conversionrate.dto.ConversionRateDto;
import nexxus.conversionrate.service.ConversionRateService;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/conversion-rates")
@RequiredArgsConstructor
public class ConversionRateController {

  private final ConversionRateService conversionRateService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @RequestBody ConversionRateDto conversionRateDto) {
    return conversionRateService.create(conversionRateDto);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findById(@PathVariable("id") String id) {
    return conversionRateService.findById(id);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findByBrandAndEnvironmentId(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return conversionRateService.findByBrandAndEnvironmentId(brandId, environmentId);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @RequestBody ConversionRateDto conversionRateDto) {
    return conversionRateService.update(id, conversionRateDto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return conversionRateService.delete(id);
  }
}
