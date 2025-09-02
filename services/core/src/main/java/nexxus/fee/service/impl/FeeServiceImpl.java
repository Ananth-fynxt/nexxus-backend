package nexxus.fee.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.fee.dto.FeeComponentDto;
import nexxus.fee.dto.FeeDetailedDto;
import nexxus.fee.dto.FeeDto;
import nexxus.fee.entity.Fee;
import nexxus.fee.entity.FeeComponent;
import nexxus.fee.entity.FeeCountry;
import nexxus.fee.entity.FeePsp;
import nexxus.fee.repository.FeeComponentRepository;
import nexxus.fee.repository.FeeCountryRepository;
import nexxus.fee.repository.FeePspRepository;
import nexxus.fee.repository.FeeRepository;
import nexxus.fee.service.FeeService;
import nexxus.flowaction.repository.FlowActionRepository;
import nexxus.psp.repository.PspRepository;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.FeeComponentType;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.service.CurrencyValidationService;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {

  private final FeeRepository feeRepository;
  private final FeeComponentRepository feeComponentRepository;
  private final FeeCountryRepository feeCountryRepository;
  private final FeePspRepository feePspRepository;
  private final FlowActionRepository flowActionRepository;
  private final PspRepository pspRepository;
  private final CurrencyValidationService currencyValidationService;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(FeeDto feeDto) {
    try {
      validateRequest(feeDto);

      return checkFeeExists(feeDto)
          .flatMap(
              existing ->
                  customError(
                      ErrorCode.FEE_ALREADY_EXISTS,
                      "Fee already exists with name: "
                          + feeDto.getName()
                          + " for the given brand, environment and flow action",
                      HttpStatus.CONFLICT))
          .switchIfEmpty(upsertFee(null, feeDto, "create"));

    } catch (Exception e) {
      return databaseError(e, "creating fee");
    }
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createFeeRecord(FeeDto feeDto) {
    Fee fee =
        Fee.create(
            feeDto.getName(),
            feeDto.getCurrency(),
            feeDto.getChargeFeeType(),
            feeDto.getBrandId(),
            feeDto.getEnvironmentId(),
            feeDto.getFlowActionId(),
            feeDto.getCreatedBy());

    return feeRepository
        .insertFee(fee)
        .then(
            Mono.defer(
                () -> {
                  return createAssociations(fee, feeDto)
                      .then(
                          Mono.defer(
                              () -> {
                                FeeDetailedDto responseDto = FeeDetailedDto.fromEntity(fee);
                                return successResponse(
                                    responseDto, "Fee and associations created successfully");
                              }));
                }))
        .onErrorResume(e -> databaseError(e, "creating fee"));
  }

  private Mono<Void> createAssociations(Fee fee, FeeDto feeDto) {
    return createCountries(fee, feeDto)
        .then(createComponents(fee, feeDto))
        .then(createPsps(fee, feeDto));
  }

  private Mono<Void> createComponents(Fee fee, FeeDto feeDto) {
    if (feeDto.getComponents() == null || feeDto.getComponents().isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(feeDto.getComponents())
        .flatMap(
            componentDto -> {
              FeeComponent component =
                  FeeComponent.create(
                      fee.getId(),
                      fee.getVersion(),
                      componentDto.getType(),
                      componentDto.getAmount(),
                      componentDto.getMinValue(),
                      componentDto.getMaxValue());
              return feeComponentRepository.insertFeeComponent(component);
            })
        .then();
  }

  private Mono<Void> createCountries(Fee fee, FeeDto feeDto) {
    if (feeDto.getCountries() == null || feeDto.getCountries().isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(feeDto.getCountries())
        .flatMap(
            country -> {
              FeeCountry feeCountry = FeeCountry.create(fee.getId(), fee.getVersion(), country);
              return feeCountryRepository.insertFeeCountry(feeCountry);
            })
        .then();
  }

  private Mono<Void> createPsps(Fee fee, FeeDto feeDto) {
    if (feeDto.getPsps() == null || feeDto.getPsps().isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(feeDto.getPsps())
        .flatMap(
            pspId -> {
              FeePsp feePsp = FeePsp.create(fee.getId(), fee.getVersion(), pspId);
              return feePspRepository.insertFeePsp(feePsp);
            })
        .then();
  }

  private Mono<Boolean> validatePspCurrencySupport(FeeDto feeDto) {
    return currencyValidationService.validatePspCurrencySupportBoolean(
        feeDto.getCurrency(),
        feeDto.getPsps(),
        feeDto.getBrandId(),
        feeDto.getEnvironmentId(),
        feeDto.getFlowActionId());
  }

  private Mono<Fee> checkFeeExists(FeeDto feeDto) {
    return feeRepository.findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
        feeDto.getBrandId(), feeDto.getEnvironmentId(),
        feeDto.getFlowActionId(), feeDto.getName());
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(String id) {
    return feeRepository
        .findLatestVersionById(id)
        .flatMap(this::buildFeeWithAssociations)
        .flatMap(feeDto -> successResponse(feeDto, "Fee retrieved successfully"))
        .switchIfEmpty(
            customError(
                ErrorCode.FEE_NOT_FOUND, "Fee not found with ID: " + id, HttpStatus.NOT_FOUND))
        .onErrorResume(e -> databaseError(e, "retrieving fee"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      String brandId, String environmentId) {
    return feeRepository
        .findByBrandIdAndEnvironmentId(brandId, environmentId)
        .flatMap(this::buildFeeWithAssociations)
        .collectList()
        .flatMap(fees -> successResponse(fees, "Fees retrieved successfully"))
        .onErrorResume(e -> databaseError(e, "retrieving fees"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getByPspId(String pspId) {
    return feePspRepository
        .findByPspId(pspId)
        .flatMap(
            (FeePsp feePsp) ->
                feeRepository.findByIdAndVersion(feePsp.getFeeId(), feePsp.getFeeVersion()))
        .flatMap((Fee fee) -> buildFeeWithAssociations(fee))
        .collectList()
        .flatMap(fees -> successResponse(fees, "Fees retrieved successfully for PSP: " + pspId))
        .onErrorResume(e -> databaseError(e, "retrieving fees for PSP"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FeeDto feeDto) {
    try {
      validateRequest(feeDto);

      return feeRepository
          .findLatestVersionById(id)
          .flatMap(existingFee -> upsertFee(existingFee, feeDto, "update"))
          .switchIfEmpty(
              customError(
                  ErrorCode.FEE_NOT_FOUND, "Fee not found with ID: " + id, HttpStatus.NOT_FOUND));

    } catch (Exception e) {
      return databaseError(e, "updating fee");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    return feeRepository
        .findLatestVersionById(id)
        .flatMap(
            fee ->
                deleteAllAssociations(fee.getId())
                    .then(feeRepository.deleteAllById(id))
                    .then(successResponse(null, "Fee deleted successfully")))
        .switchIfEmpty(
            customError(
                ErrorCode.FEE_NOT_FOUND, "Fee not found with ID: " + id, HttpStatus.NOT_FOUND))
        .onErrorResume(e -> databaseError(e, "deleting fee"));
  }

  private Mono<FeeDetailedDto> buildFeeWithAssociations(Fee fee) {
    return Mono.zip(
            feeComponentRepository
                .findByFeeIdAndFeeVersion(fee.getId(), fee.getVersion())
                .collectList(),
            feeCountryRepository
                .findByFeeIdAndFeeVersion(fee.getId(), fee.getVersion())
                .collectList(),
            feePspRepository.findByFeeIdAndFeeVersion(fee.getId(), fee.getVersion()).collectList())
        .flatMap(
            tuple -> {
              return buildDetailedFeeResponse(fee, tuple.getT1(), tuple.getT2(), tuple.getT3());
            });
  }

  private Mono<Void> deleteAllAssociations(String feeId) {
    return Mono.when(
        feeComponentRepository.deleteByFeeId(feeId),
        feeCountryRepository.deleteByFeeId(feeId),
        feePspRepository.deleteByFeeId(feeId));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> upsertFee(
      Fee existingFee, FeeDto feeDto, String operation) {
    String validationCurrency =
        existingFee != null ? existingFee.getCurrency() : feeDto.getCurrency();
    String validationBrandId = existingFee != null ? existingFee.getBrandId() : feeDto.getBrandId();
    String validationEnvironmentId =
        existingFee != null ? existingFee.getEnvironmentId() : feeDto.getEnvironmentId();
    String validationFlowActionId =
        existingFee != null ? existingFee.getFlowActionId() : feeDto.getFlowActionId();

    FeeDto validationDto =
        FeeDto.builder()
            .currency(validationCurrency)
            .brandId(validationBrandId)
            .environmentId(validationEnvironmentId)
            .flowActionId(validationFlowActionId)
            .psps(feeDto.getPsps())
            .build();

    return validatePspCurrencySupport(validationDto)
        .flatMap(
            validationResult -> {
              if (!validationResult) {
                return currencyValidationService
                    .getUnsupportedPsps(
                        validationCurrency,
                        feeDto.getPsps(),
                        validationBrandId,
                        validationEnvironmentId,
                        validationFlowActionId)
                    .flatMap(
                        unsupportedPsps ->
                            customError(
                                ErrorCode.FEE_CONFIGURATION_ERROR,
                                "Currency "
                                    + validationCurrency
                                    + " is not supported by PSPs: "
                                    + String.join(", ", unsupportedPsps),
                                HttpStatus.BAD_REQUEST));
              }

              if ("create".equals(operation)) {
                return createFeeRecord(feeDto);
              } else {
                return updateFeeRecord(existingFee, feeDto);
              }
            });
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateFeeRecord(
      Fee existingFee, FeeDto feeDto) {
    try {
      Integer newVersion = existingFee.getVersion() + 1;

      Fee updatedFee =
          Fee.createNewVersion(
              existingFee,
              feeDto.getName(),
              feeDto.getCurrency(),
              feeDto.getChargeFeeType(),
              existingFee.getBrandId(),
              existingFee.getEnvironmentId(),
              feeDto.getFlowActionId(),
              newVersion,
              feeDto.getUpdatedBy());

      return feeRepository
          .insertFee(updatedFee)
          .then(
              Mono.defer(
                  () -> {
                    return createAssociations(updatedFee, feeDto)
                        .then(
                            Mono.defer(
                                () -> {
                                  FeeDetailedDto responseDto =
                                      FeeDetailedDto.fromEntity(updatedFee);
                                  return successResponse(
                                      responseDto,
                                      "Fee version " + newVersion + " created successfully");
                                }));
                  }))
          .onErrorResume(e -> databaseError(e, "creating new fee version"));
    } catch (Exception e) {
      return databaseError(e, "creating new fee version");
    }
  }

  private void validateRequest(FeeDto feeDto) {
    validateNotNull(feeDto, "Fee DTO");
    validateNotBlank(feeDto.getName(), "Fee name");
    validateNotBlank(feeDto.getCurrency(), "Currency");
    validateNotBlank(feeDto.getBrandId(), "Brand ID");
    validateNotBlank(feeDto.getEnvironmentId(), "Environment ID");
    validateNotBlank(feeDto.getFlowActionId(), "Flow Action ID");
    validateNotNull(feeDto.getChargeFeeType(), "Charge fee type");
    validateNotNull(feeDto.getComponents(), "Components");
    validateCondition(!feeDto.getComponents().isEmpty(), "At least one component is required");
    validateNotNull(feeDto.getCountries(), "Countries");
    validateCondition(!feeDto.getCountries().isEmpty(), "At least one country is required");
    validateNotNull(feeDto.getPsps(), "PSPs");
    validateCondition(!feeDto.getPsps().isEmpty(), "At least one PSP is required");
    validateFeeComponents(feeDto.getComponents());
  }

  private void validateFeeComponents(java.util.List<FeeComponentDto> components) {
    if (components == null || components.isEmpty()) {
      throw new ValidationException("At least one component is required");
    }

    Set<FeeComponentType> allowedTypes =
        Set.of(
            FeeComponentType.FIXED, FeeComponentType.FIXED_PER_UNIT, FeeComponentType.PERCENTAGE);

    for (FeeComponentDto component : components) {
      validateNotNull(component.getType(), "Component type");
      validateNotNull(component.getAmount(), "Component amount");

      if (!allowedTypes.contains(component.getType())) {
        throw new ValidationException(
            "Invalid component type: "
                + component.getType()
                + ". Allowed types are: "
                + allowedTypes);
      }

      if (component.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValidationException("Component amount must be greater than 0");
      }

      if (component.getMinValue() != null && component.getMaxValue() != null) {
        if (component.getMinValue().compareTo(component.getMaxValue()) >= 0) {
          throw new ValidationException("Component min value must be less than max value");
        }
      }
    }

    long uniqueComponentCount =
        components.stream().map(FeeComponentDto::getType).distinct().count();

    if (uniqueComponentCount != components.size()) {
      throw new ValidationException("Fee cannot contain duplicate component types");
    }
  }

  private Mono<FeeDetailedDto> buildDetailedFeeResponse(
      Fee fee, List<FeeComponent> components, List<FeeCountry> countries, List<FeePsp> psps) {
    return Mono.zip(
            flowActionRepository
                .findById(fee.getFlowActionId())
                .map(flowAction -> flowAction.getName())
                .defaultIfEmpty("Unknown Flow Action"),
            Flux.fromIterable(psps)
                .flatMap(
                    feePsp ->
                        pspRepository
                            .findById(feePsp.getPspId())
                            .map(
                                psp ->
                                    FeeDetailedDto.FeeDetailedPspDto.builder()
                                        .id(feePsp.getPspId())
                                        .name(psp.getName())
                                        .build())
                            .defaultIfEmpty(
                                FeeDetailedDto.FeeDetailedPspDto.builder()
                                    .id(feePsp.getPspId())
                                    .name("Unknown PSP")
                                    .build()))
                .collectList(),
            Flux.fromIterable(countries).map(country -> country.getCountry()).collectList(),
            Flux.fromIterable(components)
                .map(
                    component ->
                        FeeDetailedDto.FeeDetailedComponentDto.builder()
                            .id(component.getId())
                            .type(component.getType())
                            .amount(component.getAmount())
                            .minValue(component.getMinValue())
                            .maxValue(component.getMaxValue())
                            .build())
                .collectList())
        .map(
            tuple -> {
              String flowActionName = tuple.getT1();
              List<FeeDetailedDto.FeeDetailedPspDto> pspDtos = tuple.getT2();
              List<String> countryDtos = tuple.getT3();
              List<FeeDetailedDto.FeeDetailedComponentDto> componentDtos = tuple.getT4();

              FeeDetailedDto baseDto = FeeDetailedDto.fromEntity(fee);
              baseDto.setFlowActionName(flowActionName);
              baseDto.setPsps(pspDtos);
              baseDto.setCountries(countryDtos);
              baseDto.setComponents(componentDtos);

              return baseDto;
            });
  }
}
