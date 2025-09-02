package nexxus.conversionrate.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import nexxus.shared.constants.ConversionFetchOption;
import nexxus.shared.constants.ConversionRateSource;
import nexxus.shared.constants.Status;
import nexxus.shared.util.IdGenerator;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("conversion_rate")
public class ConversionRate {
  @Id
  @Column("id")
  private String id;

  @Column("version")
  private Integer version;

  @Column("source_type")
  private ConversionRateSource sourceType;

  @Column("fetch_option")
  private ConversionFetchOption fetchOption;

  @Column("custom_url")
  private String customUrl;

  @Column("brand_id")
  private String brandId;

  @Column("environment_id")
  private String environmentId;

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

  public static ConversionRate create(
      ConversionRateSource sourceType,
      ConversionFetchOption fetchOption,
      String customUrl,
      String brandId,
      String environmentId,
      String createdBy) {
    return ConversionRate.builder()
        .id(IdGenerator.generateConversionRateId())
        .version(1)
        .sourceType(sourceType)
        .fetchOption(fetchOption)
        .customUrl(customUrl)
        .brandId(brandId)
        .environmentId(environmentId)
        .status(Status.ENABLED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .createdBy(createdBy != null ? createdBy : "system")
        .updatedBy(createdBy != null ? createdBy : "system")
        .build();
  }

  public static ConversionRate createNewVersion(
      ConversionRate existingConversionRate,
      ConversionRateSource sourceType,
      ConversionFetchOption fetchOption,
      String customUrl,
      String brandId,
      String environmentId,
      Integer newVersion,
      String updatedBy) {
    return ConversionRate.builder()
        .id(existingConversionRate.getId())
        .version(newVersion)
        .sourceType(sourceType)
        .fetchOption(fetchOption)
        .customUrl(customUrl)
        .brandId(brandId)
        .environmentId(environmentId)
        .status(existingConversionRate.getStatus())
        .createdAt(existingConversionRate.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .createdBy(existingConversionRate.getCreatedBy())
        .updatedBy(updatedBy != null ? updatedBy : "system")
        .build();
  }

  /**
   * Note: This method is deprecated for updates since the system uses versioning. Updates should
   * create new version records instead of modifying existing ones. This method is kept for backward
   * compatibility but should not be used for versioning updates.
   */
  @Deprecated
  public void updateDetails(
      ConversionRateSource sourceType,
      ConversionFetchOption fetchOption,
      String customUrl,
      String brandId,
      String environmentId,
      Status status,
      String updatedBy) {
    this.sourceType = sourceType;
    this.fetchOption = fetchOption;
    this.customUrl = customUrl;
    this.brandId = brandId;
    this.environmentId = environmentId;
    this.status = status != null ? status : this.status;
    this.updatedBy = updatedBy;
    this.updatedAt = LocalDateTime.now();
    // Removed version increment - versioning is handled by creating new records
  }
}
