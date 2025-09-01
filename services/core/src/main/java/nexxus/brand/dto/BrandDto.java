package nexxus.brand.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.brand.entity.Brand;

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
public class BrandDto {
  private String id;

  @NotBlank(message = "Brand name is required")
  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  public static BrandDto fromEntity(Brand brand) {
    return BrandDto.builder()
        .id(brand.getId())
        .name(brand.getName())
        .createdAt(brand.getCreatedAt())
        .updatedAt(brand.getUpdatedAt())
        .build();
  }
}
