package nexxus.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.shared.constants.TransactionStatus;
import nexxus.transaction.entity.Transaction;

import jakarta.validation.constraints.DecimalMin;
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
public class TransactionDto {
  private String id;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private BigDecimal amount;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow action ID is required")
  private String flowActionId;

  private String routingRuleId;

  @NotNull(message = "User attribute is required")
  private UserAttributeDto userAttribute;

  private TransactionStatus status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  public static TransactionDto fromEntity(Transaction transaction) {
    return TransactionDto.builder()
        .id(transaction.getId())
        .amount(transaction.getAmount())
        .currency(transaction.getCurrency())
        .brandId(transaction.getBrandId())
        .environmentId(transaction.getEnvironmentId())
        .flowActionId(transaction.getFlowActionId())
        .routingRuleId(transaction.getRoutingRuleId())
        .status(transaction.getStatus())
        .createdAt(transaction.getCreatedAt())
        .build();
  }
}
