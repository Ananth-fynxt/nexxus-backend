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
public class FeeId implements Serializable {
  private String id;
  private Integer version;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    FeeId that = (FeeId) obj;
    return Objects.equals(id, that.id) && Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, version);
  }

  @Override
  public String toString() {
    return "FeeId{" + "id='" + id + '\'' + ", version=" + version + '}';
  }
}
