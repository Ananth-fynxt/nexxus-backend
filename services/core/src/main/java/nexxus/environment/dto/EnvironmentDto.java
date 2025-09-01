package nexxus.environment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import nexxus.environment.entity.Environment;

import jakarta.validation.constraints.NotBlank;
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
public class EnvironmentDto {
  private String id;

  @NotBlank(message = "Environment name is required")
  private String name;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Origin URL is required")
  private String origin;

  @JsonIgnore private String secret;

  @JsonIgnore private String token;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static EnvironmentDto fromEntity(Environment environment) {
    return EnvironmentDto.builder()
        .id(environment.getId())
        .name(environment.getName())
        .brandId(environment.getBrandId())
        .origin(environment.getOrigin())
        .secret(environment.getSecret())
        .token(environment.getToken())
        .createdAt(environment.getCreatedAt())
        .updatedAt(environment.getUpdatedAt())
        .createdBy(environment.getCreatedBy())
        .updatedBy(environment.getUpdatedBy())
        .build();
  }
}
