package nexxus.brand.entity;

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
@Table("brands")
public class Brand {
  @Id private String id;

  private String name;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  public static Brand create(String name) {
    return Brand.builder()
        .id(IdGenerator.generateBrandId())
        .name(name)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public void updateDetails(String name) {
    this.name = name;
    this.updatedAt = LocalDateTime.now();
  }
}
