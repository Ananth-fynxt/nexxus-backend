package nexxus.fee.dto;

import java.math.BigDecimal;

import nexxus.fee.entity.FeeComponent;
import nexxus.shared.constants.FeeComponentType;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeComponentDto {
  private String id;

  @NotNull(message = "Component type is required")
  private FeeComponentType type;

  @NotNull(message = "Component amount is required")
  private BigDecimal amount;

  private BigDecimal minValue;

  private BigDecimal maxValue;

  public static FeeComponentDto fromEntity(FeeComponent feeComponent) {
    return FeeComponentDto.builder()
        .id(feeComponent.getId())
        .type(feeComponent.getType())
        .amount(feeComponent.getAmount())
        .minValue(feeComponent.getMinValue())
        .maxValue(feeComponent.getMaxValue())
        .build();
  }
}
