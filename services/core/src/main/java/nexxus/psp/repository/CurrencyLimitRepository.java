package nexxus.psp.repository;

import java.math.BigDecimal;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.CurrencyLimit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CurrencyLimitRepository extends R2dbcRepository<CurrencyLimit, String> {

  @Query(
      "SELECT * FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId")
  Flux<CurrencyLimit> findByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Modifying
  @Query(
      "INSERT INTO currency_limits (brand_id, environment_id, flow_action_id, psp_id, currency, min_value, max_value) VALUES (:brandId, :environmentId, :flowActionId, :pspId, :currency, :minValue, :maxValue)")
  Mono<Void> insertCurrencyLimit(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId,
      @Param("currency") String currency,
      @Param("minValue") BigDecimal minValue,
      @Param("maxValue") BigDecimal maxValue);

  @Modifying
  @Query(
      "DELETE FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId")
  Mono<Void> deleteByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Query("SELECT * FROM currency_limits WHERE psp_id = :pspId")
  Flux<CurrencyLimit> findByPspId(@Param("pspId") String pspId);

  @Query(
      "SELECT COUNT(*) > 0 FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId AND currency = :currency")
  Mono<Boolean> existsByCompositeKeyAndCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId,
      @Param("currency") String currency);

  @Query(
      "SELECT psp_id FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND currency = :currency")
  Flux<String> findSupportedPspIdsByCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("currency") String currency);
}
