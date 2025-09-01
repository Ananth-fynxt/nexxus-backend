package nexxus.conversionrate.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.conversionrate.dto.ConversionRateDto;
import nexxus.conversionrate.entity.ConversionRate;
import nexxus.conversionrate.repository.ConversionRateMarkupValueRepository;
import nexxus.conversionrate.repository.ConversionRateRepository;
import nexxus.conversionrate.service.ConversionRateService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConversionRateServiceImpl implements ConversionRateService {

  private final ConversionRateRepository conversionRateRepository;
  private final ConversionRateMarkupValueRepository conversionRateMarkupValueRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(ConversionRateDto conversionRateDto) {
    try {
      validateCreateRequest(conversionRateDto);
      return validateCreateCurrencies(conversionRateDto) // 🔑 new check
          .then(createConversionRate(conversionRateDto))
          .onErrorResume(e -> databaseError(e, "creating conversion rate config"));
    } catch (IllegalArgumentException e) {
      return validationError(e.getMessage());
    } catch (Exception e) {
      return databaseError(e, "creating conversion rate config");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findById(String id) {
    try {
      return conversionRateRepository
          .findLatestById(id)
          .flatMap(
              config -> {
                return conversionRateMarkupValueRepository
                    .findByConversionRateConfigIdAndConversionRateConfigVersion(
                        config.getId(), config.getVersion())
                    .next()
                    .map(
                        markupValue ->
                            ConversionRateDto.fromEntityWithMarkupValue(config, markupValue))
                    .map(
                        configDto ->
                            responseHandler.successResponse(
                                configDto, "Conversion rate config found successfully"));
              })
          .switchIfEmpty(
              Mono.just(
                  responseHandler.errorResponse(
                      ErrorCode.CONVERSION_RATE_NOT_FOUND,
                      "Conversion Rate Config not found with ID: " + id,
                      org.springframework.http.HttpStatus.NOT_FOUND)))
          .onErrorResume(e -> databaseError(e, "finding Conversion Rate Config"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findByBrandAndEnvironmentId(
      String brandId, String environmentId) {
    try {
      return conversionRateRepository
          .findLatestByBrandAndEnvironmentId(brandId, environmentId)
          .flatMap(
              config -> {
                return conversionRateMarkupValueRepository
                    .findByConversionRateConfigIdAndConversionRateConfigVersion(
                        config.getId(), config.getVersion())
                    .next()
                    .map(
                        markupValue ->
                            ConversionRateDto.fromEntityWithMarkupValue(config, markupValue));
              })
          .collectList()
          .map(
              configDtos ->
                  responseHandler.successResponse(
                      configDtos, "Conversion rate configs found successfully"))
          .onErrorResume(e -> databaseError(e, "finding conversion rate configs"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, ConversionRateDto dto) {
    try {
      validateUpdateRequest(dto);
      return validateUpdateCurrencies(id, dto) // 🔑 new check
          .then(conversionRateRepository.findLatestById(id))
          .flatMap(
              existingConfig -> {
                try {
                  Integer newVersion = existingConfig.getVersion() + 1;

                  ConversionRate newConfig =
                      ConversionRate.builder()
                          .id(existingConfig.getId())
                          .version(newVersion)
                          .sourceType(dto.getSourceType())
                          .fetchOption(dto.getFetchOption())
                          .brandId(dto.getBrandId())
                          .environmentId(dto.getEnvironmentId())
                          .status(
                              dto.getStatus() != null
                                  ? dto.getStatus()
                                  : existingConfig.getStatus())
                          .createdAt(existingConfig.getCreatedAt())
                          .updatedAt(java.time.LocalDateTime.now())
                          .createdBy(existingConfig.getCreatedBy())
                          .updatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : "system")
                          .build();

                  return conversionRateRepository
                      .insertConversionRate(
                          newConfig.getId(),
                          newConfig.getVersion(),
                          newConfig.getSourceType(),
                          newConfig.getFetchOption(),
                          newConfig.getBrandId(),
                          newConfig.getEnvironmentId(),
                          newConfig.getStatus(),
                          newConfig.getCreatedAt(),
                          newConfig.getUpdatedAt(),
                          newConfig.getCreatedBy(),
                          newConfig.getUpdatedBy())
                      .then(
                          Mono.defer(
                              () -> {
                                return createMarkupValue(
                                        newConfig.getId(), newConfig.getVersion(), dto)
                                    .then(
                                        Mono.defer(
                                            () -> {
                                              ConversionRateDto responseDto =
                                                  ConversionRateDto.fromEntity(newConfig);
                                              return Mono.just(
                                                  responseHandler.successResponse(
                                                      responseDto,
                                                      "Conversion Rate Config version "
                                                          + newVersion
                                                          + " created successfully"));
                                            }));
                              }));
                } catch (Exception e) {
                  return databaseError(e, "creating new Conversion Rate Config version");
                }
              })
          .switchIfEmpty(
              Mono.just(
                  responseHandler.errorResponse(
                      ErrorCode.CONVERSION_RATE_NOT_FOUND,
                      "Conversion Rate Config not found with ID: " + id,
                      org.springframework.http.HttpStatus.NOT_FOUND)))
          .onErrorResume(e -> databaseError(e));
    } catch (IllegalArgumentException e) {
      return validationError(e.getMessage());
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    try {
      return conversionRateRepository
          .findAllById(id)
          .collectList()
          .flatMap(
              conversionRateConfigs -> {
                if (conversionRateConfigs.isEmpty()) {
                  return Mono.just(
                      responseHandler.errorResponse(
                          ErrorCode.CONVERSION_RATE_NOT_FOUND,
                          "Conversion Rate Config not found with ID: " + id,
                          org.springframework.http.HttpStatus.NOT_FOUND));
                }

                return Flux.fromIterable(conversionRateConfigs)
                    .flatMap(
                        config ->
                            conversionRateMarkupValueRepository.deleteByConfigIdAndVersion(
                                config.getId(), config.getVersion()))
                    .collectList()
                    .then(Mono.just(conversionRateConfigs))
                    .flatMap(
                        configs -> {
                          return Flux.fromIterable(configs)
                              .flatMap(
                                  config -> conversionRateRepository.deleteAllById(config.getId()))
                              .collectList()
                              .then(
                                  Mono.just(
                                      responseHandler.successResponse(
                                          null,
                                          "Conversion Rate Config and all versions deleted successfully")));
                        });
              })
          .onErrorResume(e -> databaseError(e, "deleting Conversion Rate Config"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  private void validateCreateRequest(ConversionRateDto dto) {
    validateNotNull(dto, "Conversion Rate Config DTO");
    validateNotNull(dto.getSourceType(), "Source Type");
    validateNotNull(dto.getFetchOption(), "Fetch option");
    validateNotBlank(dto.getBrandId(), "Brand ID");
    validateNotBlank(dto.getEnvironmentId(), "Environment ID");
    validateNotNull(dto.getMarkupOption(), "Markup Option");
    validateNotBlank(dto.getSourceCurrency(), "Source Currency");
    validateNotBlank(dto.getTargetCurrency(), "Target Currency");
    validateNotNull(dto.getAmount(), "Amount");
  }

  private void validateUpdateRequest(ConversionRateDto dto) {
    validateNotNull(dto, "Conversion Rate Config DTO");
    validateNotNull(dto.getSourceType(), "Source Type");
    validateNotNull(dto.getFetchOption(), "Fetch option");
    validateNotBlank(dto.getBrandId(), "Brand ID");
    validateNotBlank(dto.getEnvironmentId(), "Environment ID");
    validateNotNull(dto.getMarkupOption(), "Markup Option");
    validateNotBlank(dto.getSourceCurrency(), "Source Currency");
    validateNotBlank(dto.getTargetCurrency(), "Target Currency");
    validateNotNull(dto.getAmount(), "Amount");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createConversionRate(ConversionRateDto dto) {
    try {
      ConversionRate config =
          ConversionRate.create(
              dto.getSourceType(),
              dto.getFetchOption(),
              dto.getBrandId(),
              dto.getEnvironmentId(),
              "system");

      return conversionRateRepository
          .insertConversionRate(
              config.getId(),
              config.getVersion(),
              config.getSourceType(),
              config.getFetchOption(),
              config.getBrandId(),
              config.getEnvironmentId(),
              config.getStatus(),
              config.getCreatedAt(),
              config.getUpdatedAt(),
              config.getCreatedBy(),
              config.getUpdatedBy())
          .then(
              Mono.defer(
                  () -> {
                    return createMarkupValue(config.getId(), config.getVersion(), dto)
                        .then(
                            Mono.defer(
                                () -> {
                                  ConversionRateDto responseDto =
                                      ConversionRateDto.fromEntity(config);
                                  return Mono.just(
                                      responseHandler.successResponse(
                                          responseDto,
                                          "Conversion rate config and markup value created successfully"));
                                }));
                  }))
          .onErrorResume(e -> databaseError(e, "creating conversion rate config"));

    } catch (Exception e) {
      return databaseError(e);
    }
  }

  private Mono<Void> createMarkupValue(
      String configId, Integer configVersion, ConversionRateDto dto) {
    return conversionRateMarkupValueRepository
        .insertMarkupValue(
            configId,
            configVersion,
            dto.getMarkupOption(),
            dto.getSourceCurrency(),
            dto.getTargetCurrency(),
            dto.getAmount())
        .then();
  }

  private Mono<Void> validateCreateCurrencies(ConversionRateDto dto) {
    return conversionRateMarkupValueRepository
        .countActiveDuplicateCurrencyPairs(
            dto.getBrandId(),
            dto.getEnvironmentId(),
            dto.getSourceCurrency(),
            dto.getTargetCurrency(),
            dto.getMarkupOption())
        .flatMap(
            count -> {
              if (count > 0) {
                return Mono.error(
                    new ValidationException(
                        ErrorCode.CONVERSION_RATE_INVALID,
                        "Duplicate conversion rate pair already exists: "
                            + dto.getSourceCurrency()
                            + " -> "
                            + dto.getTargetCurrency()
                            + " with markup option "
                            + dto.getMarkupOption(),
                        null));
              }
              return Mono.empty();
            });
  }

  private Mono<Void> validateUpdateCurrencies(String id, ConversionRateDto dto) {
    // For updates: Only check if the currency pair exists in OTHER configs (not the same config ID)
    return conversionRateMarkupValueRepository
        .countActiveDuplicateCurrencyPairsExcludingConfigId(
            dto.getBrandId(),
            dto.getEnvironmentId(),
            id,
            dto.getSourceCurrency(),
            dto.getTargetCurrency(),
            dto.getMarkupOption())
        .flatMap(
            count -> {
              if (count > 0) {
                return Mono.error(
                    new ValidationException(
                        ErrorCode.CONVERSION_RATE_INVALID,
                        "Duplicate conversion rate pair already exists in another config: "
                            + dto.getSourceCurrency()
                            + " -> "
                            + dto.getTargetCurrency()
                            + " with markup option "
                            + dto.getMarkupOption(),
                        null));
              }
              return Mono.empty();
            });
  }
}
