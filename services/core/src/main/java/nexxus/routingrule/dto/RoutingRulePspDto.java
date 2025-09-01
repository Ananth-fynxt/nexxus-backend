package nexxus.routingrule.dto;

import nexxus.routingrule.entity.RoutingRulePsp;

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
public class RoutingRulePspDto {

  @NotBlank(message = "PSP ID is required")
  private String pspId;

  private Integer pspValue;

  public static RoutingRulePspDto fromEntity(RoutingRulePsp psp) {
    return RoutingRulePspDto.builder().pspId(psp.getPspId()).pspValue(psp.getPspValue()).build();
  }
}
