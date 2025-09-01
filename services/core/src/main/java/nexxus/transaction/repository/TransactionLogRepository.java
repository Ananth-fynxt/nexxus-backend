package nexxus.transaction.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.transaction.entity.TransactionLog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionLogRepository extends ReactiveCrudRepository<TransactionLog, String> {

  Flux<TransactionLog> findByTransactionId(String transactionId);

  @Modifying
  @Query(
      "INSERT INTO transaction_logs (id, transaction_id, psp_id, webhook_id, log, created_at) VALUES (:id, :transactionId, :pspId, :webhookId, :log::jsonb, :createdAt)")
  Mono<Void> insertTransactionLog(
      @Param("id") String id,
      @Param("transactionId") String transactionId,
      @Param("pspId") String pspId,
      @Param("webhookId") String webhookId,
      @Param("log") String log,
      @Param("createdAt") LocalDateTime createdAt);
}
