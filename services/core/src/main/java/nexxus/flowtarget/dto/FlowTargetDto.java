package nexxus.flowtarget.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import nexxus.flowtarget.entity.FlowTarget;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class FlowTargetDto {
  private String id;

  @NotBlank(message = "Flow target name is required")
  private String name;

  @NotBlank(message = "Flow target logo is required")
  private String logo;

  @NotNull(message = "Flow target status is required")
  private Status status;

  @NotBlank(message = "Credential schema is required")
  private String credentialSchema;

  @Builder.Default private String inputSchema = "{}";

  private List<String> currencies;

  private List<String> countries;

  private List<String> paymentMethods;

  private String flowTypeId;

  private String brandId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  @JsonProperty("supportedActions")
  private List<SupportedActionInfo> supportedActions;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SupportedActionInfo {
    @JsonProperty("flowDefinitionId")
    private String id;

    private String flowActionId;

    @JsonProperty("flowActionName")
    private String flowActionName;
  }

  public static FlowTargetDto fromEntity(FlowTarget flowTarget) {
    return FlowTargetDto.builder()
        .id(flowTarget.getId())
        .name(flowTarget.getName())
        .logo(flowTarget.getLogo())
        .status(flowTarget.getStatus())
        .credentialSchema(
            flowTarget.getCredentialSchema() != null
                ? flowTarget.getCredentialSchema().asString()
                : "{}")
        .inputSchema(
            flowTarget.getInputSchema() != null ? flowTarget.getInputSchema().asString() : "{}")
        .currencies(flowTarget.getCurrencies())
        .countries(flowTarget.getCountries())
        .paymentMethods(flowTarget.getPaymentMethods())
        .flowTypeId(flowTarget.getFlowTypeId())
        .brandId(flowTarget.getBrandId())
        .createdAt(flowTarget.getCreatedAt())
        .updatedAt(flowTarget.getUpdatedAt())
        .createdBy(flowTarget.getCreatedBy())
        .updatedBy(flowTarget.getUpdatedBy())
        .build();
  }
}
