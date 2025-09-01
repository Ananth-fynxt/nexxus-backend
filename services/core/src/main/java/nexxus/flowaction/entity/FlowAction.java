package nexxus.flowaction.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("flow_actions")
public class FlowAction {
  @Id private String id;

  private String name;

  @Column("steps")
  private List<String> steps;

  @Column("flow_type_id")
  private String flowTypeId;

  @Column("input_schema")
  private Json inputSchema;

  @Column("output_schema")
  private Json outputSchema;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static FlowAction create(
      String name, List<String> steps, String flowTypeId, String inputSchema, String outputSchema) {
    return FlowAction.builder()
        .id(IdGenerator.generateFlowActionId())
        .name(name)
        .steps(steps)
        .flowTypeId(flowTypeId)
        .inputSchema(Json.of(inputSchema))
        .outputSchema(Json.of(outputSchema))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy("system")
        .updatedBy("system")
        .build();
  }

  public void updateDetails(
      String name, List<String> steps, String inputSchema, String outputSchema) {
    this.name = name;
    this.steps = steps;
    this.inputSchema = Json.of(inputSchema);
    this.outputSchema = Json.of(outputSchema);
    this.updatedAt = LocalDateTime.now();
  }
}
