package nexxus.transaction.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.transaction.entity.CrmCustomer;

import reactor.core.publisher.Mono;

@Repository
public interface CrmCustomerRepository extends ReactiveCrudRepository<CrmCustomer, String> {

  @Query(
      "SELECT COUNT(*) FROM crm_customer WHERE brand_id = :brandId AND environment_id = :environmentId AND crm_customer_id = :crmCustomerId")
  Mono<Long> existsByBrandIdAndEnvironmentIdAndCrmCustomerId(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("crmCustomerId") String crmCustomerId);

  @Modifying
  @Query(
      "INSERT INTO crm_customer (brand_id, environment_id, crm_customer_id, name, email, tag, country, account_type, custom_attributes, created_at) VALUES (:brandId, :environmentId, :crmCustomerId, :name, :email, :tag, :country, :accountType, :customAttributes::jsonb, :createdAt)")
  Mono<Void> insertCrmCustomer(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("crmCustomerId") String crmCustomerId,
      @Param("name") String name,
      @Param("email") String email,
      @Param("tag") String tag,
      @Param("country") String country,
      @Param("accountType") String accountType,
      @Param("customAttributes") String customAttributes,
      @Param("createdAt") LocalDateTime createdAt);
}
