package nexxus.riskrule.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.riskrule.entity.RiskRule;
import nexxus.riskrule.entity.RiskRuleId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RiskRuleRepository extends ReactiveCrudRepository<RiskRule, RiskRuleId> {

  @Modifying
  @Query(
      "INSERT INTO risk_rule (id, version, name, type, action, currency, duration, criteria_type, criteria_value, max_amount, brand_id, environment_id, flow_action_id, status, created_at, updated_at, created_by, updated_by) VALUES (:id, :version, :name, :type::\"risk_type\", :action::\"risk_action\", :currency, :duration::\"risk_duration\", :criteriaType::\"risk_customer_criteria_type\", :criteriaValue, :maxAmount, :brandId, :environmentId, :flowActionId, :status::\"status\", :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Integer> insertRiskRule(
      @Param("id") String id,
      @Param("version") Integer version,
      @Param("name") String name,
      @Param("type") String type,
      @Param("action") String action,
      @Param("currency") String currency,
      @Param("duration") String duration,
      @Param("criteriaType") String criteriaType,
      @Param("criteriaValue") String criteriaValue,
      @Param("maxAmount") BigDecimal maxAmount,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("status") String status,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Query("SELECT * FROM risk_rule WHERE id = :id ORDER BY version DESC LIMIT 1")
  Mono<RiskRule> findLatestById(@Param("id") String id);

  @Query("SELECT * FROM risk_rule WHERE id = :id ORDER BY version DESC")
  Flux<RiskRule> findAllById(@Param("id") String id);

  @Query(
      "SELECT DISTINCT ON (id) * FROM risk_rule WHERE brand_id = :brandId AND environment_id = :environmentId ORDER BY id, version DESC")
  Flux<RiskRule> findLatestByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Modifying
  @Query("DELETE FROM risk_rule WHERE id = :id AND version = :version")
  Mono<Integer> deleteByIdAndVersion(@Param("id") String id, @Param("version") Integer version);

  @Modifying
  @Query("DELETE FROM risk_rule WHERE id = :id")
  Mono<Integer> deleteAllById(@Param("id") String id);
}
