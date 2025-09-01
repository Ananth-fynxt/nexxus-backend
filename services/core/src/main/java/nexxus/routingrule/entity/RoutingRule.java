package nexxus.routingrule.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.PSPSelectionMode;
import nexxus.shared.constants.Status;
import nexxus.shared.util.IdGenerator;

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
@Table("routing_rules")
public class RoutingRule {

  @Id private RoutingRuleId routingRuleId;

  @Column("id")
  private String id;

  @Column("version")
  private Integer version;

  @Column("name")
  private String name;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("psp_selection_mode")
  private PSPSelectionMode pspSelectionMode;

  @Column("condition_json")
  private Json conditionJson;

  @Column("is_default")
  private Boolean isDefault;

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

  public static RoutingRule create(
      String name,
      String brandId,
      String environmentId,
      PSPSelectionMode pspSelectionMode,
      String conditionJson,
      Boolean isDefault,
      String createdBy) {
    String id = IdGenerator.generateRoutingRuleId();
    return RoutingRule.builder()
        .routingRuleId(new RoutingRuleId(id, 1))
        .id(id)
        .version(1)
        .name(name)
        .brandId(brandId)
        .environmentId(environmentId)
        .pspSelectionMode(pspSelectionMode)
        .conditionJson(Json.of(conditionJson))
        .isDefault(isDefault)
        .status(Status.ENABLED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy(createdBy != null ? createdBy : "system")
        .updatedBy(createdBy != null ? createdBy : "system")
        .build();
  }

  public static RoutingRule createNewVersion(
      RoutingRule existingRoutingRule,
      String name,
      String brandId,
      String environmentId,
      PSPSelectionMode pspSelectionMode,
      String conditionJson,
      Boolean isDefault,
      Integer newVersion,
      String updatedBy) {
    return RoutingRule.builder()
        .routingRuleId(new RoutingRuleId(existingRoutingRule.getId(), newVersion))
        .id(existingRoutingRule.getId())
        .version(newVersion)
        .name(name)
        .brandId(brandId)
        .environmentId(environmentId)
        .pspSelectionMode(pspSelectionMode)
        .conditionJson(Json.of(conditionJson))
        .isDefault(isDefault)
        .status(existingRoutingRule.getStatus())
        .createdAt(existingRoutingRule.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .createdBy(existingRoutingRule.getCreatedBy())
        .updatedBy(updatedBy != null ? updatedBy : "system")
        .build();
  }
}
