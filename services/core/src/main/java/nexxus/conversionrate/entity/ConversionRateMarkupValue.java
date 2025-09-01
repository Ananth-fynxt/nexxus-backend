package nexxus.conversionrate.entity;

import java.math.BigDecimal;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.ConversionMarkupOption;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("conversion_rate_markup_values")
public class ConversionRateMarkupValue {
  @Column("conversion_rate_config_id")
  private String conversionRateConfigId;

  @Column("conversion_rate_config_version")
  private Integer conversionRateConfigVersion;

  @Column("markup_option")
  private ConversionMarkupOption markupOption;

  @Column("source_currency")
  private String sourceCurrency;

  @Column("target_currency")
  private String targetCurrency;

  @Column("amount")
  private BigDecimal amount;

  public static ConversionRateMarkupValue create(
      String conversionRateConfigId,
      Integer conversionRateConfigVersion,
      ConversionMarkupOption markupOption,
      String sourceCurrency,
      String targetCurrency,
      BigDecimal amount) {
    return ConversionRateMarkupValue.builder()
        .conversionRateConfigId(conversionRateConfigId)
        .conversionRateConfigVersion(conversionRateConfigVersion)
        .markupOption(markupOption)
        .sourceCurrency(sourceCurrency)
        .targetCurrency(targetCurrency)
        .amount(amount)
        .build();
  }
}
