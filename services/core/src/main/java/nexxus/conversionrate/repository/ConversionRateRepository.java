package nexxus.conversionrate.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.conversionrate.entity.ConversionRate;
import nexxus.shared.constants.ConversionFetchOption;
import nexxus.shared.constants.ConversionRateSource;
import nexxus.shared.constants.Status;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for ConversionRate entity with composite primary key (id, version). Note: We use
 * Object as ID type since Spring Data R2DBC doesn't natively support composite primary keys. Custom
 * query methods handle the composite key logic.
 */
@Repository
public interface ConversionRateRepository extends ReactiveCrudRepository<ConversionRate, Object> {

  @Query(
      "SELECT * FROM conversion_rate WHERE brand_id = :brandId AND environment_id = :environmentId")
  Flux<ConversionRate> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT * FROM conversion_rate WHERE source_type = :sourceType AND fetch_option = :fetchOption AND version = :version")
  Mono<ConversionRate> findBySourceTypeAndFetchOptionAndVersion(
      @Param("sourceType") ConversionRateSource sourceType,
      @Param("fetchOption") ConversionFetchOption fetchOption,
      @Param("version") Integer version);

  @Query("SELECT * FROM conversion_rate WHERE id = :id ORDER BY version DESC LIMIT 1")
  Mono<ConversionRate> findLatestById(@Param("id") String id);

  @Query(
      "SELECT DISTINCT ON (id) * FROM conversion_rate WHERE brand_id = :brandId AND environment_id = :environmentId ORDER BY id, version DESC")
  Flux<ConversionRate> findLatestByBrandAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query("SELECT * FROM conversion_rate WHERE id = :id ORDER BY version DESC")
  Flux<ConversionRate> findAllById(@Param("id") String id);

  @Modifying
  @Query(
      "INSERT INTO conversion_rate (id, version, source_type, custom_url, fetch_option, brand_id, environment_id, status, created_at, updated_at, created_by, updated_by) VALUES (:id, :version, :sourceType::conversion_rate_source, :customUrl, :fetchOption::conversion_fetch_option, :brandId, :environmentId, :status::status, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Integer> insertConversionRate(
      @Param("id") String id,
      @Param("version") Integer version,
      @Param("sourceType") ConversionRateSource sourceType,
      @Param("customUrl") String customUrl,
      @Param("fetchOption") ConversionFetchOption fetchOption,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") Status status,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query(
      "UPDATE conversion_rate SET source_type = :sourceType, custom_url = :customUrl, fetch_option = :fetchOption, brand_id = :brandId, environment_id = :environmentId, status = :status, updated_at = :updatedAt, updated_by = :updatedBy WHERE id = :id")
  Mono<Integer> updateConversionRate(
      @Param("id") String id,
      @Param("sourceType") ConversionRateSource sourceType,
      @Param("customUrl") String customUrl,
      @Param("fetchOption") ConversionFetchOption fetchOption,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") Status status,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query("DELETE FROM conversion_rate WHERE id = :id")
  Mono<Integer> deleteAllById(@Param("id") String id);

  @Modifying
  @Query(
      "UPDATE conversion_rate SET status = 'DISABLED'::status, updated_at = :updatedAt, updated_by = :updatedBy WHERE id = :id AND version = (SELECT MAX(version) FROM conversion_rate WHERE id = :id)")
  Mono<Integer> disableLatestVersion(
      @Param("id") String id,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("updatedBy") String updatedBy);
}
