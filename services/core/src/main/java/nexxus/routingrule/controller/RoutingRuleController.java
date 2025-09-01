package nexxus.routingrule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.routingrule.dto.UpdateRoutingRuleDto;
import nexxus.routingrule.service.RoutingRuleService;
import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/routing-rules")
@RequiredArgsConstructor
public class RoutingRuleController extends BaseController {

  private final RoutingRuleService routingRuleService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @Validated @RequestBody RoutingRuleDto routingRuleDto) {
    return routingRuleService.create(routingRuleDto);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(@PathVariable String id) {
    return routingRuleService.getById(id);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @Validated @RequestBody UpdateRoutingRuleDto updateRoutingRuleDto) {
    return routingRuleService.update(id, updateRoutingRuleDto);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return routingRuleService.delete(id);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> readAllByBrandAndEnvironment(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return routingRuleService.readAllByBrandAndEnvironment(brandId, environmentId);
  }
}
