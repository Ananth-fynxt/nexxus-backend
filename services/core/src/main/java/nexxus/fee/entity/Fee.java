package nexxus.fee.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.ChargeFeeType;
import nexxus.shared.constants.Status;
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
@Table("fee")
public class Fee {

  @Id private FeeId feeId;

  @Column("id")
  private String id;

  @Column("version")
  private Integer version;

  @Column("name")
  private String name;

  @Column("currency")
  private String currency;

  @Column("charge_fee_type")
  private ChargeFeeType chargeFeeType;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("status")
  private Status status;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static Fee create(
      String name,
      String currency,
      ChargeFeeType chargeFeeType,
      String brandId,
      String environmentId,
      String flowActionId,
      String createdBy) {
    String generatedId = IdGenerator.generateFeeId();
    Integer version = 1;

    return Fee.builder()
        .feeId(new FeeId(generatedId, version))
        .id(generatedId)
        .version(version)
        .name(name)
        .currency(currency)
        .chargeFeeType(chargeFeeType)
        .brandId(brandId)
        .environmentId(environmentId)
        .flowActionId(flowActionId)
        .status(Status.ENABLED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy("system")
        .updatedBy("system")
        .build();
  }

  public static Fee createNewVersion(
      Fee existingFee,
      String name,
      String currency,
      ChargeFeeType chargeFeeType,
      String brandId,
      String environmentId,
      String flowActionId,
      Integer newVersion,
      String updatedBy) {
    return Fee.builder()
        .feeId(new FeeId(existingFee.getId(), newVersion))
        .id(existingFee.getId())
        .version(newVersion)
        .name(name)
        .currency(currency)
        .chargeFeeType(chargeFeeType)
        .brandId(brandId)
        .environmentId(environmentId)
        .flowActionId(flowActionId)
        .status(Status.ENABLED)
        .createdAt(existingFee.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .createdBy(existingFee.getCreatedBy())
        .updatedBy(updatedBy != null ? updatedBy : "system")
        .build();
  }
}
