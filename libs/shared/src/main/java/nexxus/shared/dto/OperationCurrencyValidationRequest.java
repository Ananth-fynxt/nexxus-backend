package nexxus.shared.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/** Request DTO for operation currency validation against flow targets. */
@Data
@Builder
public class OperationCurrencyValidationRequest {

  private String flowTargetId;
  private List<PspOperation> operations;

  @Data
  @Builder
  public static class PspOperation {
    private String flowActionId;
    private String flowDefinitionId;
    private List<CurrencyInfo> currencies;
  }

  @Data
  @Builder
  public static class CurrencyInfo {
    private String currency;
    private String minValue;
    private String maxValue;
  }
}
