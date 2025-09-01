package nexxus.flowtarget.entity;

import java.time.LocalDateTime;
import java.util.List;

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
@Table("flow_targets")
public class FlowTarget {
  @Id private String id;

  private String name;

  private String logo;

  private Status status;

  @Column("credential_schema")
  private Json credentialSchema;

  @Column("input_schema")
  private Json inputSchema;

  @Column("currencies")
  private List<String> currencies;

  @Column("flow_type_id")
  private String flowTypeId;

  @Column("brand_id")
  private String brandId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static FlowTarget create(
      String name,
      String logo,
      Status status,
      String credentialSchema,
      String inputSchema,
      List<String> currencies,
      String flowTypeId,
      String brandId) {
    return FlowTarget.builder()
        .id(IdGenerator.generateFlowTargetId())
        .name(name)
        .logo(logo)
        .status(status != null ? status : Status.ENABLED)
        .credentialSchema(Json.of(credentialSchema != null ? credentialSchema : "{}"))
        .inputSchema(Json.of(inputSchema != null ? inputSchema : "{}"))
        .currencies(currencies != null ? currencies : List.of())
        .flowTypeId(flowTypeId)
        .brandId(brandId)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy("system")
        .updatedBy("system")
        .build();
  }

  public void updateDetails(
      String name,
      String logo,
      Status status,
      String credentialSchema,
      String inputSchema,
      List<String> currencies,
      String brandId) {
    this.name = name;
    this.logo = logo;
    this.status = status != null ? status : this.status;
    this.credentialSchema =
        credentialSchema != null ? Json.of(credentialSchema) : this.credentialSchema;
    this.inputSchema = inputSchema != null ? Json.of(inputSchema) : this.inputSchema;
    this.currencies = currencies != null ? currencies : this.currencies;
    this.brandId = brandId != null ? brandId : this.brandId;
    this.updatedAt = LocalDateTime.now();
  }
}
