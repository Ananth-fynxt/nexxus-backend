package nexxus.webhook.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.webhook.entity.Webhook;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WebhookRepository extends ReactiveCrudRepository<Webhook, String> {

  @Modifying
  @Query(
      "INSERT INTO webhooks (id, status_type, url, retry, brand_id, environment_id, created_at, updated_at, created_by, updated_by) VALUES (:id, :statusType::webhook_status_type, :url, :retry, :brandId, :environmentId, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertWebhook(
      @Param("id") String id,
      @Param("statusType") String statusType,
      @Param("url") String url,
      @Param("retry") Integer retry,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Query("SELECT * FROM webhooks WHERE id = :id")
  Mono<Webhook> findById(@Param("id") String id);

  @Query("SELECT * FROM webhooks WHERE brand_id = :brandId AND environment_id = :environmentId")
  Flux<Webhook> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Modifying
  @Query(
      "UPDATE webhooks SET url = :url, retry = :retry, status = :status::\"status\", updated_at = :updatedAt, updated_by = :updatedBy WHERE id = :id")
  Mono<Void> updateWebhook(
      @Param("id") String id,
      @Param("url") String url,
      @Param("retry") Integer retry,
      @Param("status") String status,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query("DELETE FROM webhooks WHERE id = :id")
  Mono<Void> deleteById(@Param("id") String id);
}
