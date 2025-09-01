package nexxus.fee.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.fee.entity.FeeComponent;
import nexxus.fee.entity.FeeComponentId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeeComponentRepository
    extends ReactiveCrudRepository<FeeComponent, FeeComponentId> {

  @Query("SELECT * FROM fee_components WHERE fee_id = :feeId AND fee_version = :feeVersion")
  Flux<FeeComponent> findByFeeIdAndFeeVersion(
      @Param("feeId") String feeId, @Param("feeVersion") Integer feeVersion);

  @Modifying
  @Query("DELETE FROM fee_components WHERE fee_id = :feeId")
  Mono<Integer> deleteByFeeId(@Param("feeId") String feeId);

  @Modifying
  @Query(
      "INSERT INTO fee_components (id, fee_id, fee_version, fee_component_type, amount, min_value, max_value) VALUES (:#{#component.id}, :#{#component.feeId}, :#{#component.feeVersion}, :#{#component.type}::fee_component_type, :#{#component.amount}, :#{#component.minValue}, :#{#component.maxValue})")
  Mono<Void> insertFeeComponent(FeeComponent component);
}
