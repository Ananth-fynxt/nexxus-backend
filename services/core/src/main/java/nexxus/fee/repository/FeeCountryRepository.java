package nexxus.fee.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.fee.entity.FeeCountry;
import nexxus.fee.entity.FeeCountryId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeeCountryRepository extends ReactiveCrudRepository<FeeCountry, FeeCountryId> {

  @Query("SELECT * FROM fee_countries WHERE fee_id = :feeId AND fee_version = :feeVersion")
  Flux<FeeCountry> findByFeeIdAndFeeVersion(
      @Param("feeId") String feeId, @Param("feeVersion") Integer feeVersion);

  @Modifying
  @Query("DELETE FROM fee_countries WHERE fee_id = :feeId")
  Mono<Integer> deleteByFeeId(@Param("feeId") String feeId);

  @Modifying
  @Query(
      "INSERT INTO fee_countries (fee_id, fee_version, country) VALUES (:#{#country.feeId}, :#{#country.feeVersion}, :#{#country.country})")
  Mono<Void> insertFeeCountry(FeeCountry country);
}
