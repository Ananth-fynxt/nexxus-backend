package nexxus.psp.entity;

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
@Table("supported_currencies")
public class SupportedCurrency {

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("psp_id")
  private String pspId;

  private String currency;
}
