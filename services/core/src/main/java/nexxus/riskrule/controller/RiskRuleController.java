package nexxus.riskrule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.riskrule.service.RiskRuleService;
import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/risk-rule")
@RequiredArgsConstructor
public class RiskRuleController extends BaseController {

  private final RiskRuleService riskRuleService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(@Validated @RequestBody RiskRuleDto dto) {
    return riskRuleService.create(dto);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findById(@PathVariable("id") String id) {
    return riskRuleService.findById(id);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findAllByBrandIdAndEnvironmentId(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return riskRuleService.findAllByBrandIdAndEnvironmentId(brandId, environmentId);
  }

  @GetMapping("/psp/{pspId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> findAllByPspId(@PathVariable String pspId) {
    return riskRuleService.findAllByPspId(pspId);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @Validated @RequestBody RiskRuleDto dto) {
    return riskRuleService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> deleteById(@PathVariable("id") String id) {
    return riskRuleService.deleteById(id);
  }
}
