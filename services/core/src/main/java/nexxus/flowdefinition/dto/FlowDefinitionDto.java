package nexxus.flowdefinition.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.flowdefinition.entity.FlowDefinition;

import jakarta.validation.constraints.NotBlank;
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
public class FlowDefinitionDto {
  private String id;

  @NotBlank(message = "Flow action ID is required")
  private String flowActionId;

  @NotBlank(message = "Flow target ID is required")
  private String flowTargetId;

  private String description;

  @NotBlank(message = "Code is required")
  private String code;

  private String brandId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static FlowDefinitionDto fromEntity(FlowDefinition flowDefinition) {
    return FlowDefinitionDto.builder()
        .id(flowDefinition.getId())
        .flowActionId(flowDefinition.getFlowActionId())
        .flowTargetId(flowDefinition.getFlowTargetId())
        .description(flowDefinition.getDescription())
        .code(flowDefinition.getCode())
        .brandId(flowDefinition.getBrandId())
        .createdAt(flowDefinition.getCreatedAt())
        .updatedAt(flowDefinition.getUpdatedAt())
        .createdBy(flowDefinition.getCreatedBy())
        .updatedBy(flowDefinition.getUpdatedBy())
        .build();
  }
}
