package nexxus.routingrule.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.routingrule.entity.RoutingRule;
import nexxus.routingrule.entity.RoutingRulePsp;
import nexxus.shared.constants.PSPSelectionMode;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class RoutingRuleDto {

  private String id;

  private Integer version;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotNull(message = "PSP selection mode is required")
  private PSPSelectionMode pspSelectionMode;

  @NotNull(message = "Condition JSON is required")
  private Object conditionJson;

  private Status status;

  @NotEmpty(message = "At least one PSP is required")
  private List<RoutingRulePspDto> psps;

  private Boolean isDefault;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static RoutingRuleDto fromEntity(RoutingRule routingRule) {
    return RoutingRuleDto.builder()
        .id(routingRule.getId())
        .version(routingRule.getVersion())
        .name(routingRule.getName())
        .brandId(routingRule.getBrandId())
        .environmentId(routingRule.getEnvironmentId())
        .pspSelectionMode(routingRule.getPspSelectionMode())
        .conditionJson(
            routingRule.getConditionJson() != null
                ? routingRule.getConditionJson().asString()
                : null)
        .isDefault(routingRule.getIsDefault())
        .status(routingRule.getStatus())
        .createdAt(routingRule.getCreatedAt())
        .updatedAt(routingRule.getUpdatedAt())
        .createdBy(routingRule.getCreatedBy())
        .updatedBy(routingRule.getUpdatedBy())
        .build();
  }

  public static RoutingRuleDto fromEntityWithAssociations(
      RoutingRule routingRule, List<RoutingRulePsp> psps) {
    RoutingRuleDto dto = fromEntity(routingRule);

    if (psps != null) {
      dto.setPsps(psps.stream().map(RoutingRulePspDto::fromEntity).collect(Collectors.toList()));
    }

    return dto;
  }
}
