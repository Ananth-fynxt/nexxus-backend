package nexxus.fee.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.fee.entity.FeePsp;
import nexxus.fee.entity.FeePspId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeePspRepository extends ReactiveCrudRepository<FeePsp, FeePspId> {

  @Query("SELECT * FROM fee_psps WHERE fee_id = :feeId AND fee_version = :feeVersion")
  Flux<FeePsp> findByFeeIdAndFeeVersion(
      @Param("feeId") String feeId, @Param("feeVersion") Integer feeVersion);

  @Query(
      "SELECT fp.*, p.name as psp_name FROM fee_psps fp LEFT JOIN psps p ON fp.psp_id = p.id WHERE fp.psp_id = :pspId")
  Flux<FeePsp> findByPspId(@Param("pspId") String pspId);

  @Modifying
  @Query("DELETE FROM fee_psps WHERE fee_id = :feeId")
  Mono<Integer> deleteByFeeId(@Param("feeId") String feeId);

  @Modifying
  @Query(
      "INSERT INTO fee_psps (fee_id, fee_version, psp_id) VALUES (:#{#psp.feeId}, :#{#psp.feeVersion}, :#{#psp.pspId})")
  Mono<Void> insertFeePsp(FeePsp psp);
}
