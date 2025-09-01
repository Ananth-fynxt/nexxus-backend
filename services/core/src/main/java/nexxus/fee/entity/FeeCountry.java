package nexxus.fee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("fee_countries")
public class FeeCountry {

  @Id private FeeCountryId feeCountryId;

  @Column("fee_id")
  private String feeId;

  @Column("fee_version")
  private Integer feeVersion;

  @Column("country")
  private String country;

  public static FeeCountry create(String feeId, Integer feeVersion, String country) {
    return FeeCountry.builder()
        .feeCountryId(new FeeCountryId(feeId, feeVersion, country))
        .feeId(feeId)
        .feeVersion(feeVersion)
        .country(country)
        .build();
  }
}
