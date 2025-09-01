package nexxus.webhook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;
import nexxus.webhook.dto.UpdateWebhookDto;
import nexxus.webhook.dto.WebhookDto;
import nexxus.webhook.service.WebhookService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController extends BaseController {

  private final WebhookService webhookService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(@RequestBody WebhookDto webhookDto) {
    return webhookService.create(webhookDto);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      @PathVariable String id, @RequestBody UpdateWebhookDto updateWebhookDto) {
    return webhookService.update(id, updateWebhookDto);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(@PathVariable("id") String id) {
    return webhookService.getById(id);
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return webhookService.getByBrandAndEnvironment(brandId, environmentId);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(@PathVariable("id") String id) {
    return webhookService.delete(id);
  }
}
