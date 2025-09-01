package nexxus.brand.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.brand.entity.Brand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BrandRepository extends ReactiveCrudRepository<Brand, String> {

  @NonNull
  @Query("SELECT * FROM brands WHERE name = :name")
  Mono<Brand> findByName(@Param("name") String name);

  @NonNull
  Flux<Brand> findAllBy(Pageable pageable);

  @Modifying
  @Query(
      "INSERT INTO brands (id, name, created_at, updated_at) VALUES (:id, :name, :createdAt, :updatedAt)")
  Mono<Void> insertBrand(
      @Param("id") String id,
      @Param("name") String name,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("UPDATE brands SET name = :name, updated_at = :updatedAt WHERE id = :id")
  Mono<Void> updateBrand(
      @Param("id") String id,
      @Param("name") String name,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  @Query("DELETE FROM brands WHERE id = :id")
  @NonNull
  Mono<Void> deleteById(@Param("id") String id);
}
