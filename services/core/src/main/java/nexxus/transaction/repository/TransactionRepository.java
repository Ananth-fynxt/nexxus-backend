package nexxus.transaction.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.shared.constants.TransactionStatus;
import nexxus.transaction.entity.Transaction;

import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {

  Mono<Transaction> findById(String id);

  @Modifying
  @Query(
      "INSERT INTO transactions (id, amount, currency, brand_id, environment_id, flow_action_id, routing_rule_id, user_attribute, status, created_at) VALUES (:id, :amount, :currency, :brandId, :environmentId, :flowActionId, :routingRuleId, :userAttribute::jsonb, :status::\"transaction_status\", :createdAt)")
  Mono<Void> insertTransaction(
      @Param("id") String id,
      @Param("amount") java.math.BigDecimal amount,
      @Param("currency") String currency,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("routingRuleId") String routingRuleId,
      @Param("userAttribute") String userAttribute,
      @Param("status") TransactionStatus status,
      @Param("createdAt") LocalDateTime createdAt);
}
