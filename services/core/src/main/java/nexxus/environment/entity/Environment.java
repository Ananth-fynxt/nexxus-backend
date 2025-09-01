package nexxus.environment.entity;

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
@Table("environments")
public class Environment {
  @Id private String id;

  private String name;

  @Column("brand_id")
  private String brandId;

  private String origin;

  private String secret;

  private String token;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_by")
  private String updatedBy;

  public static Environment create(String name, String brandId, String origin) {
    return Environment.builder()
        .id(IdGenerator.generateEnvironmentId())
        .name(name)
        .brandId(brandId)
        .origin(origin)
        .secret(IdGenerator.generateEnvironmentSecretId())
        .token(IdGenerator.generateTokenId())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy("system")
        .updatedBy("system")
        .build();
  }

  public void rotateSecret() {
    this.secret = IdGenerator.generateEnvironmentSecretId();
    this.updatedAt = LocalDateTime.now();
  }

  public void updateDetails(String name, String brandId, String origin) {
    this.name = name;
    this.brandId = brandId;
    this.origin = origin;
    this.updatedAt = LocalDateTime.now();
  }
}
