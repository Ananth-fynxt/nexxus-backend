package nexxus.flowtype.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("flow_types")
public class FlowType {
  @Id private String id;

  private String name;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static FlowType create(String name) {
    return FlowType.builder()
        .id(IdGenerator.generateFlowTypeId())
        .name(name)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy("system")
        .updatedBy("system")
        .build();
  }

  public void updateDetails(String name) {
    this.name = name;
    this.updatedAt = LocalDateTime.now();
  }
}
