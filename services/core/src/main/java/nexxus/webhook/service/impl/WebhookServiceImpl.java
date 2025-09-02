package nexxus.webhook.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.util.ReactiveResponseHandler;
import nexxus.webhook.dto.UpdateWebhookDto;
import nexxus.webhook.dto.WebhookDto;
import nexxus.webhook.entity.Webhook;
import nexxus.webhook.repository.WebhookRepository;
import nexxus.webhook.service.WebhookService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

  private final WebhookRepository webhookRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(WebhookDto webhookDto) {
    try {
      validateRequest(webhookDto);

      Webhook webhook =
          Webhook.create(
              webhookDto.getStatusType(),
              webhookDto.getUrl(),
              webhookDto.getRetry(),
              webhookDto.getBrandId(),
              webhookDto.getEnvironmentId(),
              "system");

      return webhookRepository
          .insertWebhook(
              webhook.getId(),
              webhook.getStatusType().name(),
              webhook.getUrl(),
              webhook.getRetry(),
              webhook.getBrandId(),
              webhook.getEnvironmentId(),
              webhook.getCreatedAt(),
              webhook.getUpdatedAt(),
              webhook.getCreatedBy(),
              webhook.getUpdatedBy())
          .then(
              Mono.defer(
                  () -> {
                    WebhookDto responseDto = WebhookDto.fromEntity(webhook);
                    return successResponse(responseDto, "Webhook created successfully");
                  }))
          .onErrorResume(e -> databaseError(e, "creating webhook"));

    } catch (Exception e) {
      return databaseError(e, "creating webhook");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      String id, UpdateWebhookDto updateWebhookDto) {
    try {
      validateNotBlank(id, "Webhook ID");
      validateUpdateRequest(updateWebhookDto);

      return webhookRepository
          .findById(id)
          .flatMap(
              existingWebhook -> {
                return webhookRepository
                    .updateWebhook(
                        id,
                        updateWebhookDto.getUrl(),
                        updateWebhookDto.getRetry(),
                        updateWebhookDto.getStatus().getValue(),
                        LocalDateTime.now(),
                        "system")
                    .then(
                        Mono.defer(
                            () -> {
                              existingWebhook.setUrl(updateWebhookDto.getUrl());
                              existingWebhook.setRetry(updateWebhookDto.getRetry());
                              existingWebhook.setStatus(updateWebhookDto.getStatus());
                              existingWebhook.setUpdatedAt(LocalDateTime.now());
                              existingWebhook.setUpdatedBy("system");

                              WebhookDto responseDto = WebhookDto.fromEntity(existingWebhook);
                              return successResponse(responseDto, "Webhook updated successfully");
                            }));
              })
          .switchIfEmpty(
              customError(
                  ErrorCode.WEBHOOK_NOT_FOUND,
                  "Webhook not found with ID: " + id,
                  HttpStatus.NOT_FOUND))
          .onErrorResume(ValidationException.class, e -> validationError(e.getMessage()));

    } catch (ValidationException e) {
      return validationError(e.getMessage());
    } catch (Exception e) {
      return databaseError(e, "updating webhook");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(String id) {
    try {
      validateNotBlank(id, "Webhook ID");

      return webhookRepository
          .findById(id)
          .flatMap(
              webhook ->
                  successResponse(WebhookDto.fromEntity(webhook), "Webhook retrieved successfully"))
          .switchIfEmpty(
              customError(
                  ErrorCode.WEBHOOK_NOT_FOUND,
                  "Webhook not found with ID: " + id,
                  HttpStatus.NOT_FOUND))
          .onErrorResume(ValidationException.class, e -> validationError(e.getMessage()));

    } catch (ValidationException e) {
      return validationError(e.getMessage());
    } catch (Exception e) {
      return databaseError(e, "retrieving webhook");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      String brandId, String environmentId) {
    try {
      validateNotBlank(brandId, "Brand ID");
      validateNotBlank(environmentId, "Environment ID");

      return webhookRepository
          .findByBrandIdAndEnvironmentId(brandId, environmentId)
          .map(WebhookDto::fromEntity)
          .collectList()
          .flatMap(webhooks -> successResponse(webhooks, "Webhooks retrieved successfully"))
          .onErrorResume(ValidationException.class, e -> validationError(e.getMessage()));

    } catch (ValidationException e) {
      return validationError(e.getMessage());
    } catch (Exception e) {
      return databaseError(e, "retrieving webhooks");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    try {
      validateNotBlank(id, "Webhook ID");

      return webhookRepository
          .findById(id)
          .flatMap(
              webhook ->
                  webhookRepository
                      .deleteById(id)
                      .then(successResponse("Webhook deleted successfully")))
          .switchIfEmpty(
              customError(
                  ErrorCode.WEBHOOK_NOT_FOUND,
                  "Webhook not found with ID: " + id,
                  HttpStatus.NOT_FOUND))
          .onErrorResume(ValidationException.class, e -> validationError(e.getMessage()));

    } catch (ValidationException e) {
      return validationError(e.getMessage());
    } catch (Exception e) {
      return databaseError(e, "deleting webhook");
    }
  }

  private void validateRequest(WebhookDto webhookDto) {
    validateNotBlank(webhookDto.getUrl(), "URL");
    validateNotBlank(webhookDto.getBrandId(), "Brand ID");
    validateNotBlank(webhookDto.getEnvironmentId(), "Environment ID");
    validateNotNull(webhookDto.getStatusType(), "Status Type");

    if (webhookDto.getRetry() != null && webhookDto.getRetry() < 0) {
      throw new ValidationException("Retry count must be non-negative");
    }
  }

  private void validateUpdateRequest(UpdateWebhookDto updateWebhookDto) {
    validateNotBlank(updateWebhookDto.getUrl(), "URL");

    if (updateWebhookDto.getRetry() != null && updateWebhookDto.getRetry() < 0) {
      throw new ValidationException("Retry count must be non-negative");
    }
  }
}
