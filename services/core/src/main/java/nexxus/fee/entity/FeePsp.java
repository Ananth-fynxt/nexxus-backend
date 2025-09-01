package nexxus.fee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("fee_psps")
public class FeePsp {

  @Id private FeePspId feePspId;

  @Column("fee_id")
  private String feeId;

  @Column("fee_version")
  private Integer feeVersion;

  @Column("psp_id")
  private String pspId;

  public static FeePsp create(String feeId, Integer feeVersion, String pspId) {
    return FeePsp.builder()
        .feePspId(new FeePspId(feeId, feeVersion, pspId))
        .feeId(feeId)
        .feeVersion(feeVersion)
        .pspId(pspId)
        .build();
  }
}
