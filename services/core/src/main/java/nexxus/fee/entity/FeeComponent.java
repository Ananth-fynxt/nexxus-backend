package nexxus.fee.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.FeeComponentType;
import nexxus.shared.util.IdGenerator;

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
@Table("fee_components")
public class FeeComponent {

  @Id private FeeComponentId feeComponentId;

  @Column("id")
  private String id;

  @Column("fee_id")
  private String feeId;

  @Column("fee_version")
  private Integer feeVersion;

  @Column("fee_component_type")
  private FeeComponentType type;

  @Column("amount")
  private BigDecimal amount;

  @Column("min_value")
  private BigDecimal minValue;

  @Column("max_value")
  private BigDecimal maxValue;

  public static FeeComponent create(
      String feeId,
      Integer feeVersion,
      FeeComponentType type,
      BigDecimal amount,
      BigDecimal minValue,
      BigDecimal maxValue) {
    String generatedId = IdGenerator.generateFeeComponentId();

    return FeeComponent.builder()
        .feeComponentId(new FeeComponentId(generatedId, feeId, feeVersion))
        .id(generatedId)
        .feeId(feeId)
        .feeVersion(feeVersion)
        .type(type)
        .amount(amount)
        .minValue(minValue)
        .maxValue(maxValue)
        .build();
  }
}
