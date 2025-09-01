package nexxus.routingrule.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.routingrule.entity.RoutingRule;
import nexxus.routingrule.entity.RoutingRuleId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoutingRuleRepository extends ReactiveCrudRepository<RoutingRule, RoutingRuleId> {

  @Modifying
  @Query(
      "INSERT INTO routing_rules (id, version, name, brand_id, environment_id, psp_selection_mode, condition_json, is_default, \"status\", created_at, updated_at, created_by, updated_by) VALUES (:id, :version, :name, :brandId, :environmentId, :pspSelectionMode::\"psp_selection_mode\", :conditionJson::jsonb, :isDefault, :status::\"status\", :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Integer> insertRoutingRule(
      @Param("id") String id,
      @Param("version") Integer version,
      @Param("name") String name,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspSelectionMode") String pspSelectionMode,
      @Param("conditionJson") String conditionJson,
      @Param("isDefault") Boolean isDefault,
      @Param("status") String status,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query("DELETE FROM routing_rules WHERE id = :id")
  Mono<Integer> deleteAllById(@Param("id") String id);

  @Query("SELECT * FROM routing_rules WHERE id = :id ORDER BY version DESC LIMIT 1")
  Mono<RoutingRule> findLatestVersionById(@Param("id") String id);

  @Query(
      "SELECT DISTINCT ON (id) * FROM routing_rules WHERE brand_id = :brandId AND environment_id = :environmentId ORDER BY id, version DESC")
  Flux<RoutingRule> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT COUNT(DISTINCT id) FROM routing_rules WHERE brand_id = :brandId AND environment_id = :environmentId")
  Mono<Long> countByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Modifying
  @Query(
      "UPDATE routing_rules SET is_default = false WHERE brand_id = :brandId AND environment_id = :environmentId AND id != :excludeId")
  Mono<Integer> updateOtherRulesToNonDefault(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("excludeId") String excludeId);
}
