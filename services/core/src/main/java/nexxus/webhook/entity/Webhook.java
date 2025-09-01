package nexxus.webhook.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.Status;
import nexxus.shared.constants.WebhookStatusType;
import nexxus.shared.util.IdGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("webhooks")
public class Webhook {

  @Id
  @Column("id")
  private String id;

  @Column("status_type")
  private WebhookStatusType statusType;

  @Column("url")
  private String url;

  @Column("retry")
  private Integer retry;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("status")
  private Status status;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static Webhook create(
      WebhookStatusType statusType,
      String url,
      Integer retry,
      String brandId,
      String environmentId,
      String createdBy) {
    return Webhook.builder()
        .id(IdGenerator.generateWebhookId())
        .statusType(statusType)
        .url(url)
        .retry(retry != null ? retry : 3)
        .brandId(brandId)
        .environmentId(environmentId)
        .status(Status.ENABLED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy(createdBy)
        .updatedBy(createdBy)
        .build();
  }
}
