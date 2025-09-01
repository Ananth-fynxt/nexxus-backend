package nexxus.webhook.service;

import org.springframework.http.ResponseEntity;

import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;
import nexxus.webhook.dto.UpdateWebhookDto;
import nexxus.webhook.dto.WebhookDto;

import reactor.core.publisher.Mono;

public interface WebhookService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(WebhookDto webhookDto);

  Mono<ResponseEntity<ApiResponse<Object>>> update(String id, UpdateWebhookDto updateWebhookDto);

  Mono<ResponseEntity<ApiResponse<Object>>> getById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      String brandId, String environmentId);

  Mono<ResponseEntity<ApiResponse<Object>>> delete(String id);
}
