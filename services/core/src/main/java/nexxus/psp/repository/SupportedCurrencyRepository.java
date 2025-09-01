package nexxus.psp.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.SupportedCurrency;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SupportedCurrencyRepository extends R2dbcRepository<SupportedCurrency, String> {

  @Query(
      "SELECT * FROM supported_currencies WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId")
  Flux<SupportedCurrency> findByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Modifying
  @Query(
      "INSERT INTO supported_currencies (brand_id, environment_id, flow_action_id, psp_id, currency) VALUES (:brandId, :environmentId, :flowActionId, :pspId, :currency)")
  Mono<Void> insertSupportedCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId,
      @Param("currency") String currency);

  @Modifying
  @Query(
      "DELETE FROM supported_currencies WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId")
  Mono<Void> deleteByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Query("SELECT * FROM supported_currencies WHERE psp_id = :pspId")
  Flux<SupportedCurrency> findByPspId(@Param("pspId") String pspId);
}
