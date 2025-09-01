package nexxus.conversionrate.repository;

import java.math.BigDecimal;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.conversionrate.entity.ConversionRateMarkupValue;
import nexxus.shared.constants.ConversionMarkupOption;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for ConversionRateMarkupValue entity with composite primary key. Note: We use Object
 * as ID type since Spring Data R2DBC doesn't natively support composite primary keys. Custom query
 * methods handle the composite key logic.
 */
@Repository
public interface ConversionRateMarkupValueRepository
    extends ReactiveCrudRepository<ConversionRateMarkupValue, Object> {

  Flux<ConversionRateMarkupValue> findByConversionRateConfigIdAndConversionRateConfigVersion(
      String conversionRateConfigId, Integer conversionRateConfigVersion);

  @Modifying
  @Query(
      "INSERT INTO conversion_rate_markup_values (conversion_rate_config_id, conversion_rate_config_version, markup_option, source_currency, target_currency, amount) VALUES (:conversionRateConfigId, :conversionRateConfigVersion, :markupOption::conversion_markup_option, :sourceCurrency, :targetCurrency, :amount)")
  Mono<Integer> insertMarkupValue(
      @Param("conversionRateConfigId") String conversionRateConfigId,
      @Param("conversionRateConfigVersion") Integer conversionRateConfigVersion,
      @Param("markupOption") ConversionMarkupOption markupOption,
      @Param("sourceCurrency") String sourceCurrency,
      @Param("targetCurrency") String targetCurrency,
      @Param("amount") BigDecimal amount);

  @Modifying
  @Query(
      "DELETE FROM conversion_rate_markup_values WHERE conversion_rate_config_id = :conversionRateConfigId AND conversion_rate_config_version = :conversionRateConfigVersion")
  Mono<Integer> deleteByConfigIdAndVersion(
      @Param("conversionRateConfigId") String conversionRateConfigId,
      @Param("conversionRateConfigVersion") Integer conversionRateConfigVersion);

  @Query(
      "SELECT COUNT(*) FROM conversion_rate_markup_values mv "
          + "JOIN conversion_rate cr ON mv.conversion_rate_config_id = cr.id AND mv.conversion_rate_config_version = cr.version "
          + "WHERE cr.brand_id = :brandId AND cr.environment_id = :environmentId "
          + "AND mv.source_currency = :sourceCurrency AND mv.target_currency = :targetCurrency "
          + "AND mv.markup_option = :markupOption::conversion_markup_option "
          + "AND cr.status = 'ENABLED'::status")
  Mono<Long> countActiveDuplicateCurrencyPairs(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("sourceCurrency") String sourceCurrency,
      @Param("targetCurrency") String targetCurrency,
      @Param("markupOption") ConversionMarkupOption markupOption);

  @Query(
      "SELECT COUNT(*) FROM conversion_rate_markup_values mv "
          + "JOIN conversion_rate cr ON mv.conversion_rate_config_id = cr.id AND mv.conversion_rate_config_version = cr.version "
          + "WHERE cr.brand_id = :brandId AND cr.environment_id = :environmentId "
          + "AND cr.id != :excludeConfigId "
          + "AND mv.source_currency = :sourceCurrency AND mv.target_currency = :targetCurrency "
          + "AND mv.markup_option = :markupOption::conversion_markup_option "
          + "AND cr.status = 'ENABLED'::status")
  Mono<Long> countActiveDuplicateCurrencyPairsExcludingConfigId(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("excludeConfigId") String excludeConfigId,
      @Param("sourceCurrency") String sourceCurrency,
      @Param("targetCurrency") String targetCurrency,
      @Param("markupOption") ConversionMarkupOption markupOption);
}
