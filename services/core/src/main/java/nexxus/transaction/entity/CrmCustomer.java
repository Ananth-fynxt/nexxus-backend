package nexxus.transaction.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("crm_customer")
public class CrmCustomer {

  @Id
  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("crm_customer_id")
  private String crmCustomerId;

  @Column("name")
  private String name;

  @Column("email")
  private String email;

  @Column("tag")
  private String tag;

  @Column("country")
  private String country;

  @Column("account_type")
  private String accountType;

  @Column("custom_attributes")
  private Json customAttributes;

  @Column("created_at")
  private LocalDateTime createdAt;

  public static CrmCustomer create(
      String brandId,
      String environmentId,
      String crmCustomerId,
      String name,
      String email,
      String tag,
      String country,
      String accountType,
      String customAttributes) {
    LocalDateTime now = LocalDateTime.now();

    return CrmCustomer.builder()
        .brandId(brandId)
        .environmentId(environmentId)
        .crmCustomerId(crmCustomerId)
        .name(name)
        .email(email)
        .tag(tag)
        .country(country)
        .accountType(accountType)
        .customAttributes(Json.of(customAttributes))
        .createdAt(now)
        .build();
  }
}
