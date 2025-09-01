package nexxus.flowtype.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.flowtype.entity.FlowType;

import reactor.core.publisher.Mono;

@Repository
public interface FlowTypeRepository extends ReactiveCrudRepository<FlowType, String> {

  Mono<FlowType> findByName(@NonNull String name);

  @Modifying
  @Query(
      "INSERT INTO flow_types (id, name, created_at, updated_at, created_by, updated_by) VALUES (:id, :name, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertFlowType(
      @Param("id") String id,
      @Param("name") String name,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query("UPDATE flow_types SET name = :name, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateFlowType(
      @Param("id") String id,
      @Param("name") String name,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("DELETE FROM flow_types WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
