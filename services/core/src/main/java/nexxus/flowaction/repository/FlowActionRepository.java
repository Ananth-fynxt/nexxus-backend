package nexxus.flowaction.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.flowaction.entity.FlowAction;

import io.r2dbc.postgresql.codec.Json;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FlowActionRepository extends ReactiveCrudRepository<FlowAction, String> {

  Flux<FlowAction> findByFlowTypeId(@Param("flowTypeId") String flowTypeId);

  Mono<FlowAction> findByNameAndFlowTypeId(
      @Param("name") String name, @Param("flowTypeId") String flowTypeId);

  @Modifying
  @Query(
      """
        INSERT INTO flow_actions (
            id, name, steps, flow_type_id, input_schema, output_schema, created_at, updated_at, created_by, updated_by
        )
        VALUES (:id, :name, :steps, :flowTypeId, :inputSchema, :outputSchema, :createdAt, :updatedAt, :createdBy, :updatedBy)
    """)
  Mono<Void> insertFlowAction(
      @Param("id") String id,
      @Param("name") String name,
      @Param("steps") String[] steps,
      @Param("flowTypeId") String flowTypeId,
      @Param("inputSchema") Json inputSchema,
      @Param("outputSchema") Json outputSchema,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query(
      "UPDATE flow_actions SET name = :name, steps = :steps, input_schema = :inputSchema, output_schema = :outputSchema, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateFlowAction(
      @Param("id") String id,
      @Param("name") String name,
      @Param("steps") String[] steps,
      @Param("inputSchema") Json inputSchema,
      @Param("outputSchema") Json outputSchema,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("DELETE FROM flow_actions WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
