package nexxus.routingrule.entity;

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
@Table("routing_rule_psps")
public class RoutingRulePsp {

  @Id private RoutingRulePspId routingRulePspId;

  @Column("routing_rule_id")
  private String routingRuleId;

  @Column("routing_rule_version")
  private Integer routingRuleVersion;

  @Column("psp_id")
  private String pspId;

  @Column("psp_value")
  private Integer pspValue;

  public static RoutingRulePsp create(
      String routingRuleId, Integer routingRuleVersion, String pspId, Integer pspValue) {
    return RoutingRulePsp.builder()
        .routingRulePspId(new RoutingRulePspId(routingRuleId, routingRuleVersion, pspId))
        .routingRuleId(routingRuleId)
        .routingRuleVersion(routingRuleVersion)
        .pspId(pspId)
        .pspValue(pspValue)
        .build();
  }
}
