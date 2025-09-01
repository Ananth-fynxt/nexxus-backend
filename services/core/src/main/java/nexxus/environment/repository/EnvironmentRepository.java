package nexxus.environment.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.environment.entity.Environment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EnvironmentRepository extends ReactiveCrudRepository<Environment, String> {

  @Query("SELECT * FROM environments WHERE brand_id = :brandId")
  Flux<Environment> findByBrandId(@Param("brandId") String brandId);

  @Query("SELECT * FROM environments WHERE name = :name AND brand_id = :brandId")
  Mono<Environment> findByNameAndBrandId(
      @Param("name") String name, @Param("brandId") String brandId);

  @Query("SELECT * FROM environments WHERE secret = :secret")
  Mono<Environment> findBySecret(@Param("secret") String secret);

  @Query("SELECT * FROM environments WHERE token = :token")
  Mono<Environment> findByToken(@Param("token") String token);

  @Modifying
  @Query(
      "INSERT INTO environments (id, name, brand_id, origin, secret, token, created_at, updated_at, created_by, updated_by) VALUES (:id, :name, :brandId, :origin, :secret, :token, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertEnvironment(
      @Param("id") String id,
      @Param("name") String name,
      @Param("brandId") String brandId,
      @Param("origin") String origin,
      @Param("secret") String secret,
      @Param("token") String token,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Modifying
  @Query("UPDATE environments SET secret = :secret, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateSecret(
      @Param("id") String id,
      @Param("secret") String secret,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query(
      "UPDATE environments SET name = :name, brand_id = :brandId, origin = :origin, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateEnvironment(
      @Param("id") String id,
      @Param("name") String name,
      @Param("brandId") String brandId,
      @Param("origin") String origin,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("DELETE FROM environments WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
