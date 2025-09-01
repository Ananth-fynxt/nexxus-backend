package nexxus.psp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.psp.dto.PspDto;
import nexxus.psp.dto.UpdatePspDto;
import nexxus.psp.service.PspService;
import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/psps")
@RequiredArgsConstructor
public class PspController extends BaseController {

  private final PspService pspService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(@Validated @RequestBody PspDto pspDto) {
    return pspService.create(pspDto);
  }

  @GetMapping("/{pspId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(@PathVariable String pspId) {
    return pspService.getById(pspId);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return pspService.getByBrandAndEnvironment(brandId, environmentId);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/{flowActionId}/{status}/{currency}")
  public Mono<ResponseEntity<ApiResponse<Object>>>
      getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
          @PathVariable String brandId,
          @PathVariable String environmentId,
          @PathVariable String flowActionId,
          @PathVariable String status,
          @PathVariable String currency) {
    return pspService.getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
        brandId, environmentId, status, currency, flowActionId);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/{flowActionId}/{status}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironmentByStatusAndFlowAction(
      @PathVariable String brandId,
      @PathVariable String environmentId,
      @PathVariable String flowActionId,
      @PathVariable String status) {
    return pspService.getByBrandAndEnvironmentByStatusAndFlowAction(
        brandId, environmentId, status, flowActionId);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/currencies")
  public Mono<ResponseEntity<ApiResponse<Object>>> getSupportedCurrenciesByBrandAndEnvironment(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return pspService.getSupportedCurrenciesByBrandAndEnvironment(brandId, environmentId);
  }

  @PutMapping("/{pspId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String pspId, @Validated @RequestBody UpdatePspDto pspDto) {
    return pspService.update(pspId, pspDto);
  }
}
