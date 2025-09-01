package nexxus.transaction.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("transaction_logs")
public class TransactionLog {

  @Id private String id;

  @Column("transaction_id")
  private String transactionId;

  @Column("psp_id")
  private String pspId;

  @Column("webhook_id")
  private String webhookId;

  @Column("log")
  private Json log;

  @Column("created_at")
  private LocalDateTime createdAt;

  public static TransactionLog create(
      String transactionId, String pspId, String webhookId, String log) {
    String generatedId = IdGenerator.generateTransactionLogId();

    return TransactionLog.builder()
        .id(generatedId)
        .transactionId(transactionId)
        .pspId(pspId)
        .webhookId(webhookId)
        .log(Json.of(log))
        .createdAt(LocalDateTime.now())
        .build();
  }
}
