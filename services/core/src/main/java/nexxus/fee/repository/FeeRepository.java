package nexxus.fee.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.fee.entity.Fee;
import nexxus.fee.entity.FeeId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeeRepository extends ReactiveCrudRepository<Fee, FeeId> {

  @Query(
      "SELECT * FROM fee WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND name = :name")
  Mono<Fee> findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("name") String name);

  @Query(
      "SELECT DISTINCT ON (id) * FROM fee WHERE brand_id = :brandId AND environment_id = :environmentId ORDER BY id, version DESC")
  Flux<Fee> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query("SELECT * FROM fee WHERE id = :id ORDER BY version DESC LIMIT 1")
  Mono<Fee> findLatestVersionById(@Param("id") String id);

  @Query("SELECT * FROM fee WHERE id = :id AND version = :version")
  Mono<Fee> findByIdAndVersion(@Param("id") String id, @Param("version") Integer version);

  @Query("SELECT COALESCE(MAX(version), 0) FROM fee WHERE id = :id")
  Mono<Integer> findMaxVersionById(@Param("id") String id);

  @Modifying
  @Query(
      "INSERT INTO fee (id, version, name, currency, charge_fee_type, brand_id, environment_id, flow_action_id, status, created_at, updated_at, created_by, updated_by) VALUES (:#{#fee.id}, :#{#fee.version}, :#{#fee.name}, :#{#fee.currency}, :#{#fee.chargeFeeType}::charge_fee_type, :#{#fee.brandId}, :#{#fee.environmentId}, :#{#fee.flowActionId}, :#{#fee.status}::status, :#{#fee.createdAt}, :#{#fee.updatedAt}, :#{#fee.createdBy}, :#{#fee.updatedBy})")
  Mono<Void> insertFee(Fee fee);

  @Modifying
  @Query("DELETE FROM fee WHERE id = :id")
  Mono<Integer> deleteAllById(@Param("id") String id);
}
