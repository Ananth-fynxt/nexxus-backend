package nexxus.riskrule.dto;

import nexxus.riskrule.entity.RiskRulePsp;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRulePspDto {
  @NotBlank(message = "PSP ID is required")
  private String id;

  public static RiskRulePspDto fromEntity(RiskRulePsp entity) {
    return RiskRulePspDto.builder().id(entity.getPspId()).build();
  }
}
