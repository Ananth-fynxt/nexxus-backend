package nexxus.fee.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeCountryId implements Serializable {
  private String feeId;
  private Integer feeVersion;
  private String country;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    FeeCountryId that = (FeeCountryId) obj;
    return Objects.equals(feeId, that.feeId)
        && Objects.equals(feeVersion, that.feeVersion)
        && Objects.equals(country, that.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(feeId, feeVersion, country);
  }

  @Override
  public String toString() {
    return "FeeCountryId{"
        + "feeId='"
        + feeId
        + '\''
        + ", feeVersion="
        + feeVersion
        + ", country='"
        + country
        + '\''
        + '}';
  }
}
