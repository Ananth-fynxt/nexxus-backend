package nexxus.flowtarget.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.flowtarget.entity.FlowTarget;

import io.r2dbc.postgresql.codec.Json;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FlowTargetRepository extends ReactiveCrudRepository<FlowTarget, String> {
  @Query("SELECT * FROM flow_targets WHERE flow_type_id = :flowTypeId")
  Flux<FlowTarget> findByFlowTypeId(String flowTypeId);

  @Modifying
  @Query(
      "INSERT INTO flow_targets (id, name, logo, status, credential_schema, input_schema, currencies, countries, payment_methods, flow_type_id, brand_id, created_at, updated_at, created_by, updated_by) VALUES (:id, :name, :logo, :status::status, :credentialSchema, :inputSchema, :currencies, :countries, :paymentMethods, :flowTypeId, :brandId, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertFlowTarget(
      @Param("id") String id,
      @Param("name") String name,
      @Param("logo") String logo,
      @Param("status") String status,
      @Param("credentialSchema") Json credentialSchema,
      @Param("inputSchema") Json inputSchema,
      @Param("currencies") String[] currencies,
      @Param("countries") String[] countries,
      @Param("paymentMethods") String[] paymentMethods,
      @Param("flowTypeId") String flowTypeId,
      @Param("brandId") String brandId,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query(
      "UPDATE flow_targets SET name = :name, logo = :logo, status = :status::status, credential_schema = :credentialSchema, input_schema = :inputSchema, currencies = :currencies, countries = :countries, payment_methods = :paymentMethods, brand_id = :brandId, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateFlowTarget(
      @Param("id") String id,
      @Param("name") String name,
      @Param("logo") String logo,
      @Param("status") String status,
      @Param("credentialSchema") Json credentialSchema,
      @Param("inputSchema") Json inputSchema,
      @Param("currencies") String[] currencies,
      @Param("countries") String[] countries,
      @Param("paymentMethods") String[] paymentMethods,
      @Param("brandId") String brandId,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("DELETE FROM flow_targets WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
