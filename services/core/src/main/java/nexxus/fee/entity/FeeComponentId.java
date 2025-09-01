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
public class FeeComponentId implements Serializable {
  private String id;
  private String feeId;
  private Integer feeVersion;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    FeeComponentId that = (FeeComponentId) obj;
    return Objects.equals(id, that.id)
        && Objects.equals(feeId, that.feeId)
        && Objects.equals(feeVersion, that.feeVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, feeId, feeVersion);
  }

  @Override
  public String toString() {
    return "FeeComponentId{"
        + "id='"
        + id
        + '\''
        + ", feeId='"
        + feeId
        + '\''
        + ", feeVersion="
        + feeVersion
        + '}';
  }
}
