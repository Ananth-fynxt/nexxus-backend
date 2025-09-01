package nexxus.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.fee.entity.Fee;
import nexxus.shared.constants.ChargeFeeType;
import nexxus.shared.constants.FeeComponentType;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeDetailedDto {

  private String id;
  private Integer version;

  @NotBlank(message = "Fee name is required")
  private String name;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotNull(message = "Charge fee type is required")
  private ChargeFeeType chargeFeeType;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private String flowActionName;
  private Status status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;
  private String updatedBy;

  @NotEmpty(message = "At least one component is required")
  private List<FeeDetailedComponentDto> components;

  @NotEmpty(message = "At least one country is required")
  private List<String> countries;

  @NotEmpty(message = "At least one PSP is required")
  private List<FeeDetailedPspDto> psps;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FeeDetailedComponentDto {
    private String id;

    @NotNull(message = "Component type is required")
    private FeeComponentType type;

    @NotNull(message = "Component amount is required")
    private BigDecimal amount;

    private BigDecimal minValue;
    private BigDecimal maxValue;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FeeDetailedPspDto {
    @NotBlank(message = "PSP ID is required")
    private String id;

    private String name;
  }

  public static FeeDetailedDto fromEntity(Fee fee) {
    return FeeDetailedDto.builder()
        .id(fee.getId())
        .version(fee.getVersion())
        .name(fee.getName())
        .currency(fee.getCurrency())
        .chargeFeeType(fee.getChargeFeeType())
        .brandId(fee.getBrandId())
        .environmentId(fee.getEnvironmentId())
        .flowActionId(fee.getFlowActionId())
        .status(fee.getStatus())
        .createdAt(fee.getCreatedAt())
        .updatedAt(fee.getUpdatedAt())
        .createdBy(fee.getCreatedBy())
        .updatedBy(fee.getUpdatedBy())
        .build();
  }
}
