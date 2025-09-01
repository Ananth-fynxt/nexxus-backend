package nexxus.routingrule.dto;

import java.util.List;

import nexxus.shared.constants.PSPSelectionMode;
import nexxus.shared.constants.Status;

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
public class UpdateRoutingRuleDto {

  private String name;

  private PSPSelectionMode pspSelectionMode;

  private Object conditionJson;

  private Boolean isDefault;

  private Status status;

  @NotEmpty(message = "At least one PSP is required")
  private List<RoutingRulePspDto> psps;

  private String updatedBy;
}
