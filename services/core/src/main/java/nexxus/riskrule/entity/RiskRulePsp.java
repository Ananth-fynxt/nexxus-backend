package nexxus.riskrule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("risk_rule_psps")
public class RiskRulePsp {
  @Id private RiskRulePspId riskRulePspId;

  @Column("risk_rule_id")
  private String riskRuleId;

  @Column("risk_rule_version")
  private Integer riskRuleVersion;

  @Column("psp_id")
  private String pspId;

  public static RiskRulePsp create(String riskRuleId, Integer riskRuleVersion, String pspId) {
    return RiskRulePsp.builder()
        .riskRulePspId(new RiskRulePspId(riskRuleId, riskRuleVersion, pspId))
        .riskRuleId(riskRuleId)
        .riskRuleVersion(riskRuleVersion)
        .pspId(pspId)
        .build();
  }
}
