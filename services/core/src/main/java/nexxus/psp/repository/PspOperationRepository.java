package nexxus.psp.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.PspOperation;
import nexxus.shared.constants.Status;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PspOperationRepository extends R2dbcRepository<PspOperation, String> {

  @Query(
      "SELECT * FROM psp_operations WHERE brand_id = :brandId AND environment_id = :environmentId AND psp_id = :pspId AND flow_action_id = :flowActionId AND flow_definition_id = :flowDefinitionId")
  Mono<PspOperation> findByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("flowDefinitionId") String flowDefinitionId);

  @Modifying
  @Query(
      "INSERT INTO psp_operations (brand_id, environment_id, psp_id, flow_action_id, flow_definition_id, status) VALUES (:brandId, :environmentId, :pspId, :flowActionId, :flowDefinitionId, :status)")
  Mono<Void> insertPspOperation(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("flowDefinitionId") String flowDefinitionId,
      @Param("status") Status status);

  @Modifying
  @Query(
      "UPDATE psp_operations SET status = :status WHERE brand_id = :brandId AND environment_id = :environmentId AND psp_id = :pspId AND flow_action_id = :flowActionId AND flow_definition_id = :flowDefinitionId")
  Mono<Void> updateStatus(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("flowDefinitionId") String flowDefinitionId,
      @Param("status") Status status);

  @Query("SELECT * FROM psp_operations WHERE psp_id = :pspId")
  Flux<PspOperation> findByPspId(@Param("pspId") String pspId);

  @Modifying
  @Query("DELETE FROM psp_operations WHERE psp_id = :pspId")
  Mono<Void> deleteByPspId(@Param("pspId") String pspId);
}
