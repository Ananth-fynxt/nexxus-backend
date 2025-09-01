package nexxus.flowtype.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.flowtype.entity.FlowType;

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
public class FlowTypeDto {
  private String id;

  @NotBlank(message = "Flow type name is required")
  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static FlowTypeDto fromEntity(FlowType flowType) {
    return FlowTypeDto.builder()
        .id(flowType.getId())
        .name(flowType.getName())
        .createdAt(flowType.getCreatedAt())
        .updatedAt(flowType.getUpdatedAt())
        .createdBy(flowType.getCreatedBy())
        .updatedBy(flowType.getUpdatedBy())
        .build();
  }
}
