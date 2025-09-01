package nexxus.psp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.psp.entity.Psp;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
public class PspDto {
  private String id;

  @NotBlank(message = "PSP name is required")
  private String name;

  private String description;

  private String logo;

  @NotBlank(message = "Credential is required")
  private String credential;

  @Positive(message = "Timeout must be greater than 0")
  private Integer timeout;

  private Boolean blockVpnAccess;

  private Boolean blockDataCenterAccess;

  private Boolean failureRate;

  @Positive(message = "Failure rate threshold must be greater than 0")
  private Integer failureRateThreshold;

  @Positive(message = "Failure rate duration minutes must be greater than 0")
  private Integer failureRateDurationMinutes;

  private List<String> ipAddress;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow target ID is required")
  private String flowTargetId;

  private Status status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  private List<MaintenanceWindowDto> maintenanceWindow;

  private List<PspOperationDto> operations;

  private FlowTargetInfo flowTarget;

  public static PspDto fromEntity(Psp psp) {
    return PspDto.builder()
        .id(psp.getId())
        .name(psp.getName())
        .description(psp.getDescription())
        .logo(psp.getLogo())
        .credential("***ENCRYPTED***") // Always set to encrypted for security
        .timeout(psp.getTimeout())
        .blockVpnAccess(psp.getBlockVpnAccess())
        .blockDataCenterAccess(psp.getBlockDataCenterAccess())
        .failureRate(psp.getFailureRate())
        .brandId(psp.getBrandId())
        .environmentId(psp.getEnvironmentId())
        .flowTargetId(psp.getFlowTargetId())
        .status(psp.getStatus())
        .createdAt(psp.getCreatedAt())
        .updatedAt(psp.getUpdatedAt())
        .createdBy(psp.getCreatedBy())
        .updatedBy(psp.getUpdatedBy())
        .failureRateThreshold(
            psp.getFailureRateThreshold() != null
                ? Math.round(psp.getFailureRateThreshold())
                : null)
        .failureRateDurationMinutes(psp.getFailureRateDurationMinutes())
        .ipAddress(psp.getIpAddress() != null ? List.of(psp.getIpAddress()) : List.of())
        .maintenanceWindow(List.of())
        .operations(List.of())
        .flowTarget(null)
        .build();
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MaintenanceWindowDto {
    private String flowActionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CurrencyDto {
    private String flowActionId;

    private String currency;

    private BigDecimal minValue;

    private BigDecimal maxValue;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PspOperationDto {
    private String flowActionId;

    private String flowDefinitionId;

    private Status status;

    private List<CurrencyDto> currencies;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SupportedActionInfo {
    private String flowActionId;

    private String flowDefinitionId;

    private String flowActionName;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FlowTargetInfo {
    private String id;

    private String credentialSchema;

    private String flowTargetName;

    private String flowTypeId;

    private List<String> supportedCurrencies;

    private List<SupportedActionInfo> supportedActions;
  }
}
