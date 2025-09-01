package nexxus.riskrule.entity;

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
public class RiskRulePspId implements Serializable {
  private String riskRuleId;
  private Integer riskRuleVersion;
  private String pspId;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    RiskRulePspId that = (RiskRulePspId) obj;
    return Objects.equals(riskRuleId, that.riskRuleId)
        && Objects.equals(riskRuleVersion, that.riskRuleVersion)
        && Objects.equals(pspId, that.pspId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(riskRuleId, riskRuleVersion, pspId);
  }

  @Override
  public String toString() {
    return "RiskRulePspId{"
        + "riskRuleId='"
        + riskRuleId
        + '\''
        + ", riskRuleVersion="
        + riskRuleVersion
        + ", pspId='"
        + pspId
        + '\''
        + '}';
  }
}
