package nexxus.riskrule.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.RiskAction;
import nexxus.shared.constants.RiskCustomerCriteriaType;
import nexxus.shared.constants.RiskDuration;
import nexxus.shared.constants.RiskType;
import nexxus.shared.constants.Status;
import nexxus.shared.util.IdGenerator;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("risk_rule")
public class RiskRule {
  @Id private RiskRuleId riskRuleId;

  @Column("id")
  private String id;

  @Column("version")
  private Integer version;

  @Column("type")
  private RiskType type;

  @Column("action")
  private RiskAction action;

  @Column("currency")
  private String currency;

  @Column("duration")
  private RiskDuration duration;

  @Column("max_amount")
  private BigDecimal maxAmount;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("name")
  private String name;

  @Column("criteria_type")
  private RiskCustomerCriteriaType criteriaType;

  @Column("criteria_value")
  private String criteriaValue;

  @Column("status")
  private Status status;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static RiskRule create(
      String name,
      RiskType type,
      RiskAction action,
      String currency,
      RiskDuration duration,
      BigDecimal maxAmount,
      String brandId,
      String environmentId,
      String flowActionId,
      RiskCustomerCriteriaType criteriaType,
      String criteriaValue) {
    String generatedId = IdGenerator.generateRiskRuleId();
    Integer version = 1;

    return RiskRule.builder()
        .riskRuleId(new RiskRuleId(generatedId, version))
        .id(generatedId)
        .version(version)
        .name(name)
        .type(type)
        .action(action)
        .currency(currency)
        .duration(duration)
        .maxAmount(maxAmount)
        .brandId(brandId)
        .environmentId(environmentId)
        .flowActionId(flowActionId)
        .criteriaType(criteriaType)
        .criteriaValue(criteriaValue)
        .status(Status.ENABLED)
        .createdBy("system")
        .updatedBy("system")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
