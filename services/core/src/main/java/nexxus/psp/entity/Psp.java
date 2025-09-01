package nexxus.psp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.Status;
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
@Table("psps")
public class Psp {
  @Id
  @Column("id")
  private String id;

  @Column("name")
  private String name;

  @Column("description")
  private String description;

  @Column("logo")
  private String logo;

  @Column("credential")
  private Json credential;

  @Column("timeout")
  private Integer timeout;

  @Column("block_vpn_access")
  private Boolean blockVpnAccess;

  @Column("block_data_center_access")
  private Boolean blockDataCenterAccess;

  @Column("failure_rate")
  private Boolean failureRate;

  @Column("ip_address")
  private String[] ipAddress;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

  @Column("flow_target_id")
  private String flowTargetId;

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

  @Column("failure_rate_threshold")
  private Float failureRateThreshold;

  @Column("failure_rate_duration_minutes")
  private Integer failureRateDurationMinutes;

  public static Psp create(
      String name,
      String description,
      String logo,
      String credential,
      Integer timeout,
      Boolean blockVpnAccess,
      Boolean blockDataCenterAccess,
      Boolean failureRate,
      Integer failureRateThreshold,
      Integer failureRateDurationMinutes,
      String[] ipAddress,
      String brandId,
      String environmentId,
      String flowTargetId,
      String createdBy) {
    return Psp.builder()
        .id(IdGenerator.generatePspId())
        .name(name)
        .description(description)
        .logo(logo)
        .credential(Json.of(credential != null ? credential : "{}"))
        .timeout(timeout != null ? timeout : 300)
        .blockVpnAccess(blockVpnAccess != null ? blockVpnAccess : false)
        .blockDataCenterAccess(blockDataCenterAccess != null ? blockDataCenterAccess : false)
        .failureRate(failureRate != null ? failureRate : false)
        .failureRateThreshold(
            failureRateThreshold != null ? failureRateThreshold.floatValue() : 0.0f)
        .failureRateDurationMinutes(
            failureRateDurationMinutes != null ? failureRateDurationMinutes : 60)
        .ipAddress(ipAddress != null ? ipAddress : new String[0])
        .brandId(brandId)
        .environmentId(environmentId)
        .flowTargetId(flowTargetId)
        .status(Status.ENABLED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy(createdBy)
        .updatedBy(createdBy)
        .build();
  }

  public void updateBasicDetails(
      String name,
      String description,
      String logo,
      String credential,
      Integer timeout,
      Boolean blockVpnAccess,
      Boolean blockDataCenterAccess,
      Boolean failureRate,
      Status status,
      Integer failureRateThreshold,
      Integer failureRateDurationMinutes,
      String[] ipAddress,
      String updatedBy) {
    if (name != null) this.name = name;
    if (description != null) this.description = description;
    if (logo != null) this.logo = logo;
    if (credential != null) this.credential = Json.of(credential);
    if (timeout != null) this.timeout = timeout;
    if (blockVpnAccess != null) this.blockVpnAccess = blockVpnAccess;
    if (blockDataCenterAccess != null) this.blockDataCenterAccess = blockDataCenterAccess;
    if (failureRate != null) this.failureRate = failureRate;
    if (status != null) this.status = status;
    if (failureRateThreshold != null) this.failureRateThreshold = failureRateThreshold.floatValue();
    if (failureRateDurationMinutes != null)
      this.failureRateDurationMinutes = failureRateDurationMinutes;
    if (ipAddress != null) this.ipAddress = ipAddress;
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = updatedBy;
  }
}
