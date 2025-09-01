package nexxus.psp.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.Psp;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PspRepository extends ReactiveCrudRepository<Psp, String> {

  @Query("SELECT * FROM psps WHERE id = :id")
  Mono<Psp> findByIdCustom(@Param("id") String id);

  @NonNull
  Mono<Psp> findByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
      @NonNull String brandId,
      @NonNull String environmentId,
      @NonNull String flowTargetId,
      @NonNull String name);

  @NonNull
  Flux<Psp> findByBrandIdAndEnvironmentId(
      @NonNull String brandId, @NonNull String environmentId, Pageable pageable);

  @Query("SELECT * FROM psps WHERE brand_id = :brandId AND environment_id = :environmentId")
  Flux<Psp> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT DISTINCT p.* FROM psps p "
          + "INNER JOIN currency_limits cl ON p.id = cl.psp_id "
          + "INNER JOIN psp_operations po ON p.id = po.psp_id "
          + "WHERE p.brand_id = :brandId AND p.environment_id = :environmentId AND p.status = :status AND po.status = :status "
          + "AND cl.currency = :currency AND po.flow_action_id = :flowActionId")
  Flux<Psp> findByBrandIdAndEnvironmentIdAndStatusAndCurrencyAndFlowAction(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") String status,
      @Param("currency") String currency,
      @Param("flowActionId") String flowActionId);

  @Query(
      "SELECT DISTINCT p.* FROM psps p "
          + "INNER JOIN psp_operations po ON p.id = po.psp_id "
          + "WHERE p.brand_id = :brandId AND p.environment_id = :environmentId AND p.status = :status AND po.status = :status "
          + "AND po.flow_action_id = :flowActionId")
  Flux<Psp> findByBrandIdAndEnvironmentIdAndStatusAndFlowAction(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") String status,
      @Param("flowActionId") String flowActionId);

  @Query(
      "SELECT DISTINCT currency FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId")
  Flux<String> findSupportedCurrenciesByBrandAndEnvironment(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Modifying
  @Query(
      "INSERT INTO psps (id, name, description, logo, credential, timeout, block_vpn_access, block_data_center_access, failure_rate, failure_rate_threshold, failure_rate_duration_minutes, ip_address, brand_id, environment_id, flow_target_id, \"status\", created_at, updated_at, created_by, updated_by) VALUES (:id, :name, :description, :logo, :credential::jsonb, :timeout, :blockVpnAccess, :blockDataCenterAccess, :failureRate, :failureRateThreshold, :failureRateDurationMinutes, :ipAddress, :brandId, :environmentId, :flowTargetId, :status, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertPsp(
      @Param("id") String id,
      @Param("name") String name,
      @Param("description") String description,
      @Param("logo") String logo,
      @Param("credential") String credential,
      @Param("timeout") Integer timeout,
      @Param("blockVpnAccess") Boolean blockVpnAccess,
      @Param("blockDataCenterAccess") Boolean blockDataCenterAccess,
      @Param("failureRate") Boolean failureRate,
      @Param("failureRateThreshold") Integer failureRateThreshold,
      @Param("failureRateDurationMinutes") Integer failureRateDurationMinutes,
      @Param("ipAddress") String[] ipAddress,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowTargetId") String flowTargetId,
      @Param("status") String status,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query(
      "UPDATE psps SET name = :name, description = :description, logo = :logo, credential = :credential::jsonb, timeout = :timeout, block_vpn_access = :blockVpnAccess, block_data_center_access = :blockDataCenterAccess, failure_rate = :failureRate, failure_rate_threshold = :failureRateThreshold, failure_rate_duration_minutes = :failureRateDurationMinutes, ip_address = :ipAddress, \"status\" = :status, updated_at = :updatedAt, updated_by = :updatedBy WHERE id = :id")
  Mono<Void> updatePspWithIpAddress(
      @Param("id") String id,
      @Param("name") String name,
      @Param("description") String description,
      @Param("logo") String logo,
      @Param("credential") String credential,
      @Param("timeout") Integer timeout,
      @Param("blockVpnAccess") Boolean blockVpnAccess,
      @Param("blockDataCenterAccess") Boolean blockDataCenterAccess,
      @Param("failureRate") Boolean failureRate,
      @Param("failureRateThreshold") Integer failureRateThreshold,
      @Param("failureRateDurationMinutes") Integer failureRateDurationMinutes,
      @Param("ipAddress") String[] ipAddress,
      @Param("status") String status,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query("DELETE FROM psps WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
