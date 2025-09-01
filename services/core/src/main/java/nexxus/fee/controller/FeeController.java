package nexxus.fee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nexxus.fee.dto.FeeDto;
import nexxus.fee.service.FeeService;
import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
public class FeeController extends BaseController {

  private final FeeService feeService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(@RequestBody FeeDto feeDto) {
    return feeService.create(feeDto);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(@PathVariable("id") String id) {
    return feeService.getById(id);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return feeService.getByBrandAndEnvironment(brandId, environmentId);
  }

  @GetMapping("/psp/{pspId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getByPspId(@PathVariable String pspId) {
    return feeService.getByPspId(pspId);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @RequestBody FeeDto feeDto) {
    return feeService.update(id, feeDto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return feeService.delete(id);
  }
}
