package nexxus.transaction.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.TransactionStatus;
import nexxus.shared.util.IdGenerator;

import io.r2dbc.postgresql.codec.Json;
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
@Table("transactions")
public class Transaction {

  @Id private String id;

  @Column("amount")
  private BigDecimal amount;

  @Column("currency")
  private String currency;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("routing_rule_id")
  private String routingRuleId;

  @Column("user_attribute")
  private Json userAttribute;

  @Column("status")
  private TransactionStatus status;

  @Column("created_at")
  private LocalDateTime createdAt;

  public static Transaction create(
      BigDecimal amount,
      String currency,
      String brandId,
      String environmentId,
      String flowActionId,
      String routingRuleId,
      String userAttribute) {
    String generatedId = IdGenerator.generateTransactionId();

    return Transaction.builder()
        .id(generatedId)
        .amount(amount)
        .currency(currency)
        .brandId(brandId)
        .environmentId(environmentId)
        .flowActionId(flowActionId)
        .routingRuleId(routingRuleId)
        .userAttribute(Json.of(userAttribute))
        .status(TransactionStatus.INITIATED)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
