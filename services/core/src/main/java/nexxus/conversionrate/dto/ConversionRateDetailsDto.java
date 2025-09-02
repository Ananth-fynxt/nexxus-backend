package nexxus.conversionrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.conversionrate.entity.ConversionRate;
import nexxus.conversionrate.entity.ConversionRateMarkupValue;
import nexxus.shared.constants.ConversionFetchOption;
import nexxus.shared.constants.ConversionRateSource;
import nexxus.shared.constants.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRateDetailsDto {

  private String id;
  private Integer version;
  private ConversionRateSource sourceType;
  private String customUrl;
  private ConversionFetchOption fetchOption;
  private String brandId;
  private String environmentId;
  private Status status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;
  private String updatedBy;

  private List<MarkupValueDto> markupValues;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MarkupValueDto {
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal amount;
    private nexxus.shared.constants.ConversionMarkupOption markupOption;
  }

  public static ConversionRateDetailsDto fromEntity(
      ConversionRate entity, List<ConversionRateMarkupValue> markupList) {
    return ConversionRateDetailsDto.builder()
        .id(entity.getId())
        .version(entity.getVersion())
        .sourceType(entity.getSourceType())
        .customUrl(entity.getCustomUrl())
        .fetchOption(entity.getFetchOption())
        .brandId(entity.getBrandId())
        .environmentId(entity.getEnvironmentId())
        .status(entity.getStatus())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .createdBy(entity.getCreatedBy())
        .updatedBy(entity.getUpdatedBy())
        .markupValues(
            markupList == null
                ? java.util.Collections.emptyList()
                : markupList.stream()
                    .map(
                        mv ->
                            MarkupValueDto.builder()
                                .markupOption(mv.getMarkupOption())
                                .sourceCurrency(mv.getSourceCurrency())
                                .targetCurrency(mv.getTargetCurrency())
                                .amount(mv.getAmount())
                                .build())
                    .collect(Collectors.toList()))
        .build();
  }
}
