package nexxus.webhook.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.shared.constants.Status;
import nexxus.shared.constants.WebhookStatusType;
import nexxus.webhook.entity.Webhook;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDto {
  private String id;

  @NotNull(message = "Status type is required")
  private WebhookStatusType statusType;

  @NotBlank(message = "URL is required")
  private String url;

  @Min(value = 0, message = "Retry count must be non-negative")
  private Integer retry;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  private Status status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static WebhookDto fromEntity(Webhook webhook) {
    return WebhookDto.builder()
        .id(webhook.getId())
        .statusType(webhook.getStatusType())
        .url(webhook.getUrl())
        .retry(webhook.getRetry())
        .brandId(webhook.getBrandId())
        .environmentId(webhook.getEnvironmentId())
        .status(webhook.getStatus())
        .createdAt(webhook.getCreatedAt())
        .updatedAt(webhook.getUpdatedAt())
        .createdBy(webhook.getCreatedBy())
        .updatedBy(webhook.getUpdatedBy())
        .build();
  }
}
