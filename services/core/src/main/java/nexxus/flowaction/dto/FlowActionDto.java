package nexxus.flowaction.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.flowaction.entity.FlowAction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class FlowActionDto {
  private String id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotEmpty(message = "Steps cannot be empty")
  private List<String> steps;

  private String flowTypeId;

  @NotBlank(message = "Input schema is required")
  private String inputSchema;

  @NotBlank(message = "Output schema is required")
  private String outputSchema;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static FlowActionDto fromEntity(FlowAction flowAction) {
    return FlowActionDto.builder()
        .id(flowAction.getId())
        .name(flowAction.getName())
        .steps(flowAction.getSteps())
        .flowTypeId(flowAction.getFlowTypeId())
        .inputSchema(
            flowAction.getInputSchema() != null ? flowAction.getInputSchema().asString() : null)
        .outputSchema(
            flowAction.getOutputSchema() != null ? flowAction.getOutputSchema().asString() : null)
        .createdAt(flowAction.getCreatedAt())
        .updatedAt(flowAction.getUpdatedAt())
        .createdBy(flowAction.getCreatedBy())
        .updatedBy(flowAction.getUpdatedBy())
        .build();
  }
}
