package nexxus.conversionrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.conversionrate.entity.ConversionRate;
import nexxus.conversionrate.entity.ConversionRateMarkupValue;
import nexxus.shared.constants.ConversionFetchOption;
import nexxus.shared.constants.ConversionMarkupOption;
import nexxus.shared.constants.ConversionRateSource;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRateDto {
  private String id;

  private Integer version;

  @NotNull(message = "Source Type is required")
  private ConversionRateSource sourceType;

  @NotNull(message = "Fetch option is required")
  private ConversionFetchOption fetchOption;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  private Status status;

  @NotNull(message = "Markup Option is required")
  private ConversionMarkupOption markupOption;

  @NotBlank(message = "Source Currency is required")
  private String sourceCurrency;

  @NotBlank(message = "Target Currency is required")
  private String targetCurrency;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
  private BigDecimal amount;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static ConversionRateDto fromEntity(ConversionRate entity) {
    return ConversionRateDto.builder()
        .id(entity.getId())
        .version(entity.getVersion())
        .sourceType(entity.getSourceType())
        .fetchOption(entity.getFetchOption())
        .brandId(entity.getBrandId())
        .environmentId(entity.getEnvironmentId())
        .status(entity.getStatus())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .createdBy(entity.getCreatedBy())
        .updatedBy(entity.getUpdatedBy())
        .build();
  }

  public static ConversionRateDto fromEntityWithMarkupValue(
      ConversionRate entity, ConversionRateMarkupValue markupValue) {
    ConversionRateDto dto = fromEntity(entity);
    if (markupValue != null) {
      dto.setMarkupOption(markupValue.getMarkupOption());
      dto.setSourceCurrency(markupValue.getSourceCurrency());
      dto.setTargetCurrency(markupValue.getTargetCurrency());
      dto.setAmount(markupValue.getAmount());
    }
    return dto;
  }
}
