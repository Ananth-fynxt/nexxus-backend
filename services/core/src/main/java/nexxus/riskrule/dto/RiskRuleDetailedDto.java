package nexxus.riskrule.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.riskrule.entity.RiskRule;
import nexxus.shared.constants.RiskAction;
import nexxus.shared.constants.RiskCustomerCriteriaType;
import nexxus.shared.constants.RiskDuration;
import nexxus.shared.constants.RiskType;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleDetailedDto {

  private String id;
  private Integer version;

  @NotNull(message = "Type is required")
  private RiskType type;

  @NotNull(message = "Action is required")
  private RiskAction action;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotNull(message = "Duration is required")
  private RiskDuration duration;

  @NotBlank(message = "Name is required")
  private String name;

  private RiskCustomerCriteriaType criteriaType;
  private String criteriaValue;

  @NotNull(message = "Max amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Max amount must be greater than 0")
  private BigDecimal maxAmount;

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

  private List<RiskRuleDetailedPspDto> psps;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RiskRuleDetailedPspDto {
    @NotBlank(message = "PSP ID is required")
    private String id;

    private String name;
  }

  public static RiskRuleDetailedDto fromEntity(RiskRule entity) {
    return RiskRuleDetailedDto.builder()
        .id(entity.getId())
        .version(entity.getVersion())
        .name(entity.getName())
        .type(entity.getType())
        .action(entity.getAction())
        .currency(entity.getCurrency())
        .duration(entity.getDuration())
        .maxAmount(entity.getMaxAmount())
        .brandId(entity.getBrandId())
        .environmentId(entity.getEnvironmentId())
        .flowActionId(entity.getFlowActionId())
        .status(entity.getStatus())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .createdBy(entity.getCreatedBy())
        .updatedBy(entity.getUpdatedBy())
        .criteriaType(RiskType.CUSTOMER.equals(entity.getType()) ? entity.getCriteriaType() : null)
        .criteriaValue(
            RiskType.CUSTOMER.equals(entity.getType()) ? entity.getCriteriaValue() : null)
        .build();
  }
}
