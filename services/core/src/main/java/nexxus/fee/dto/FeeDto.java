package nexxus.fee.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.fee.entity.Fee;
import nexxus.fee.entity.FeeComponent;
import nexxus.fee.entity.FeeCountry;
import nexxus.fee.entity.FeePsp;
import nexxus.shared.constants.ChargeFeeType;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Fee name is required")
  private String name;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotNull(message = "Charge fee type is required")
  private ChargeFeeType chargeFeeType;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private Status status;

  @NotEmpty(message = "At least one component is required")
  private List<FeeComponentDto> components;

  @NotEmpty(message = "At least one country is required")
  private List<String> countries;

  @NotEmpty(message = "At least one PSP is required")
  private List<String> psps;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  public static FeeDto fromEntity(Fee fee) {
    return FeeDto.builder()
        .id(fee.getId())
        .version(fee.getVersion())
        .name(fee.getName())
        .currency(fee.getCurrency())
        .chargeFeeType(fee.getChargeFeeType())
        .brandId(fee.getBrandId())
        .environmentId(fee.getEnvironmentId())
        .flowActionId(fee.getFlowActionId())
        .status(fee.getStatus())
        .createdAt(fee.getCreatedAt())
        .updatedAt(fee.getUpdatedAt())
        .createdBy(fee.getCreatedBy())
        .updatedBy(fee.getUpdatedBy())
        .build();
  }

  public static FeeDto fromEntityWithAssociations(
      Fee fee, List<FeeComponent> components, List<FeeCountry> countries, List<FeePsp> psps) {
    FeeDto dto = fromEntity(fee);

    if (components != null) {
      List<FeeComponentDto> componentDtos =
          components.stream().map(FeeComponentDto::fromEntity).collect(Collectors.toList());
      dto.setComponents(componentDtos);
    }

    if (countries != null) {
      List<String> countryList =
          countries.stream().map(FeeCountry::getCountry).collect(Collectors.toList());
      dto.setCountries(countryList);
    }

    if (psps != null) {
      List<String> pspList = psps.stream().map(FeePsp::getPspId).collect(Collectors.toList());
      dto.setPsps(pspList);
    }

    return dto;
  }
}
