package nexxus.psp.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.Status;

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
@Table("psp_operations")
public class PspOperation {

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("psp_id")
  private String pspId;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("flow_definition_id")
  private String flowDefinitionId;

  private Status status;

  public static PspOperation create(
      String brandId,
      String environmentId,
      String pspId,
      String flowActionId,
      String flowDefinitionId) {
    return PspOperation.builder()
        .brandId(brandId)
        .environmentId(environmentId)
        .pspId(pspId)
        .flowActionId(flowActionId)
        .flowDefinitionId(flowDefinitionId)
        .status(Status.ENABLED)
        .build();
  }

  public void updateStatus(Status status) {
    this.status = status;
  }
}
