package nexxus.flowdefinition.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.flowdefinition.entity.FlowDefinition;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FlowDefinitionRepository extends ReactiveCrudRepository<FlowDefinition, String> {

  @Query("SELECT * FROM flow_definitions WHERE flow_target_id = :flowTargetId")
  Flux<FlowDefinition> findByFlowTargetId(@Param("flowTargetId") String flowTargetId);

  @Query("SELECT * FROM flow_definitions WHERE brand_id = :brandId")
  Flux<FlowDefinition> findByBrandId(@Param("brandId") String brandId);

  @Query(
      "SELECT * FROM flow_definitions WHERE flow_target_id = :flowTargetId AND (brand_id = :brandId OR brand_id IS NULL)")
  Flux<FlowDefinition> findByFlowTargetIdAndBrandId(
      @Param("flowTargetId") String flowTargetId, @Param("brandId") String brandId);

  @Query(
      "SELECT * FROM flow_definitions WHERE code = :code AND (brand_id = :brandId OR brand_id IS NULL)")
  Mono<FlowDefinition> findByCodeAndBrandId(
      @Param("code") String code, @Param("brandId") String brandId);

  @Query(
      "SELECT * FROM flow_definitions WHERE flow_target_id = :flowTargetId AND flow_action_id = :flowActionId")
  Mono<FlowDefinition> findByFlowTargetIdAndFlowActionId(
      @Param("flowTargetId") String flowTargetId, @Param("flowActionId") String flowActionId);

  @Modifying
  @Query(
      "INSERT INTO flow_definitions (id, flow_action_id, flow_target_id, description, code, brand_id, created_at, updated_at, created_by, updated_by) VALUES (:id, :flowActionId, :flowTargetId, :description, :code, :brandId, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertFlowDefinition(
      @Param("id") String id,
      @Param("flowActionId") String flowActionId,
      @Param("flowTargetId") String flowTargetId,
      @Param("description") String description,
      @Param("code") String code,
      @Param("brandId") String brandId,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query(
      "UPDATE flow_definitions SET flow_action_id = :flowActionId, flow_target_id = :flowTargetId, description = :description, code = :code, brand_id = :brandId, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateFlowDefinition(
      @Param("id") String id,
      @Param("flowActionId") String flowActionId,
      @Param("flowTargetId") String flowTargetId,
      @Param("description") String description,
      @Param("code") String code,
      @Param("brandId") String brandId,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("DELETE FROM flow_definitions WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
