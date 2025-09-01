package nexxus.psp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("maintenance_windows")
public class MaintenanceWindow {

  @Id private String id;

  @Column("psp_id")
  private String pspId;

  @Column("flow_action_id")
  private String flowActionId;

  @Column("start_at")
  private LocalDateTime startAt;

  @Column("end_at")
  private LocalDateTime endAt;

  private Status status;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static MaintenanceWindow create(
      String pspId,
      String flowActionId,
      LocalDateTime startAt,
      LocalDateTime endAt,
      String createdBy) {
    return MaintenanceWindow.builder()
        .id(IdGenerator.generateMaintenanceWindowId())
        .pspId(pspId)
        .flowActionId(flowActionId)
        .startAt(startAt)
        .endAt(endAt)
        .status(Status.ENABLED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy(createdBy)
        .updatedBy(createdBy)
        .build();
  }

  public void updateDetails(
      LocalDateTime startAt, LocalDateTime endAt, Status status, String updatedBy) {
    if (startAt != null) this.startAt = startAt;
    if (endAt != null) this.endAt = endAt;
    if (status != null) this.status = status;
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = updatedBy;
  }
}
