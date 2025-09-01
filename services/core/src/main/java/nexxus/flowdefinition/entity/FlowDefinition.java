package nexxus.flowdefinition.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.util.IdGenerator;

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
@Table("flow_definitions")
public class FlowDefinition {
  @Id private String id;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("flow_target_id")
  private String flowTargetId;

  private String description;

  private String code;

  @Column("brand_id")
  private String brandId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static FlowDefinition create(
      String flowActionId, String flowTargetId, String description, String code, String brandId) {
    return FlowDefinition.builder()
        .id(IdGenerator.generateFlowDefinitionId())
        .flowActionId(flowActionId)
        .flowTargetId(flowTargetId)
        .description(description)
        .code(code)
        .brandId(brandId)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy("system")
        .updatedBy("system")
        .build();
  }

  public void updateDetails(
      String flowActionId, String flowTargetId, String description, String code, String brandId) {
    this.flowActionId = flowActionId;
    this.flowTargetId = flowTargetId;
    this.description = description;
    this.code = code;
    this.brandId = brandId;
    this.updatedAt = LocalDateTime.now();
  }
}
