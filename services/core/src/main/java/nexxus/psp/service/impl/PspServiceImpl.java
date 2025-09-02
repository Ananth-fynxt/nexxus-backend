package nexxus.psp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowaction.repository.FlowActionRepository;
import nexxus.flowdefinition.repository.FlowDefinitionRepository;
import nexxus.flowtarget.entity.FlowTarget;
import nexxus.flowtarget.repository.FlowTargetRepository;
import nexxus.flowtarget.service.FlowTargetService;
import nexxus.psp.dto.PspDetailsDto;
import nexxus.psp.dto.PspDto;
import nexxus.psp.dto.PspSummaryDto;
import nexxus.psp.dto.UpdatePspDto;
import nexxus.psp.entity.CurrencyLimit;
import nexxus.psp.entity.MaintenanceWindow;
import nexxus.psp.entity.Psp;
import nexxus.psp.entity.PspOperation;
import nexxus.psp.repository.CurrencyLimitRepository;
import nexxus.psp.repository.MaintenanceWindowRepository;
import nexxus.psp.repository.PspOperationRepository;
import nexxus.psp.repository.PspRepository;
import nexxus.psp.service.PspService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.dto.OperationCurrencyValidationRequest;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.service.OperationCurrencyValidationService;
import nexxus.shared.util.CryptoUtil;
import nexxus.shared.util.ReactiveResponseHandler;
import nexxus.shared.validation.CredentialValidationService;
import nexxus.shared.validation.SchemaValidationException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PspServiceImpl implements PspService {

  private final PspRepository pspRepository;
  private final ReactiveResponseHandler responseHandler;
  private final CredentialValidationService credentialValidationService;
  private final FlowTargetService flowTargetService;
  private final CryptoUtil cryptoUtil;
  private final MaintenanceWindowRepository maintenanceWindowRepository;
  private final PspOperationRepository pspOperationRepository;
  private final CurrencyLimitRepository currencyLimitRepository;
  private final FlowTargetRepository flowTargetRepository;
  private final FlowDefinitionRepository flowDefinitionRepository;
  private final FlowActionRepository flowActionRepository;
  private final OperationCurrencyValidationService operationCurrencyValidationService;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(PspDto pspDto) {
    try {
      validateCreateRequest(pspDto);

      Mono<Void> validationMono =
          pspDto.getCredential() != null
              ? validateCredentialAgainstFlowTarget(
                  pspDto.getFlowTargetId(), pspDto.getCredential())
              : Mono.empty();

      return validationMono
          .then(
              Mono.defer(
                  () -> {
                    return pspRepository
                        .findByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
                            pspDto.getBrandId(),
                            pspDto.getEnvironmentId(),
                            pspDto.getFlowTargetId(),
                            pspDto.getName())
                        .hasElement()
                        .flatMap(
                            exists -> {
                              if (exists) {
                                return customError(
                                    ErrorCode.PSP_ALREADY_EXISTS,
                                    "PSP already exists with name: "
                                        + pspDto.getName()
                                        + " for the given brand, environment and flow target",
                                    org.springframework.http.HttpStatus.CONFLICT);
                              } else {
                                return createPsp(pspDto);
                              }
                            });
                  }))
          .onErrorResume(
              SchemaValidationException.class,
              e ->
                  customError(
                      ErrorCode.PSP_CONFIGURATION_ERROR,
                      "Invalid credentials: " + e.getMessage(),
                      org.springframework.http.HttpStatus.BAD_REQUEST));

    } catch (Exception e) {
      return databaseError(e, "creating PSP");
    }
  }

  /**
   * Validates PSP credentials against the flow target's credential schema
   *
   * <p>Fetches the actual flow target and validates against its credential schema
   */
  private Mono<Void> validateCredentialAgainstFlowTarget(String flowTargetId, String credential) {
    return flowTargetService
        .getFlowTargetById(flowTargetId)
        .flatMap(
            flowTarget -> {
              String credentialSchema = flowTarget.getCredentialSchema().asString();

              try {
                credentialValidationService.validateCredential(
                    flowTargetId, credentialSchema, credential);
                return Mono.empty();
              } catch (Exception e) {
                throw e;
              }
            })
        .then();
  }

  /**
   * Encrypts credential JSON string by parsing it and encrypting each value
   *
   * @param credentialJson JSON string representation of credentials
   * @return Encrypted credential JSON string
   * @throws Exception if encryption fails
   */
  private String encryptCredential(String credentialJson) throws Exception {
    if (credentialJson == null || credentialJson.trim().isEmpty()) {
      return credentialJson;
    }

    try {
      // Parse the JSON credential to a Map
      Map<String, String> credentialMap = cryptoUtil.parseCredentialJson(credentialJson);

      // Encrypt each value in the credential map
      Map<String, String> encryptedMap = cryptoUtil.encryptCredential(credentialMap);

      // Convert back to JSON string
      return cryptoUtil.credentialMapToJson(encryptedMap);
    } catch (Exception e) {
      throw new ValidationException("Failed to encrypt credential: " + e.getMessage());
    }
  }

  private void validateCreateRequest(PspDto pspDto) {
    validateNotNull(pspDto, "PSP DTO");
    validateNotBlank(pspDto.getName(), "PSP name");
    validateNotBlank(pspDto.getCredential(), "PSP credential");
    validateNotBlank(pspDto.getBrandId(), "Brand ID");
    validateNotBlank(pspDto.getEnvironmentId(), "Environment ID");
    validateNotBlank(pspDto.getFlowTargetId(), "Flow target ID");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createPsp(PspDto pspDto) {
    try {
      String encryptedCredentialJson = encryptCredential(pspDto.getCredential());

      Psp psp =
          Psp.create(
              pspDto.getName(),
              pspDto.getDescription(),
              pspDto.getLogo(),
              encryptedCredentialJson,
              pspDto.getTimeout(),
              pspDto.getBlockVpnAccess(),
              pspDto.getBlockDataCenterAccess(),
              pspDto.getFailureRate(),
              pspDto.getFailureRateThreshold() != null
                  ? pspDto.getFailureRateThreshold().intValue()
                  : null,
              pspDto.getFailureRateDurationMinutes(),
              null, // ipAddress - will be set separately if needed
              pspDto.getBrandId(),
              pspDto.getEnvironmentId(),
              pspDto.getFlowTargetId(),
              pspDto.getCreatedBy() != null ? pspDto.getCreatedBy() : "system");

      return pspRepository
          .insertPsp(
              psp.getId(),
              psp.getName(),
              psp.getDescription(),
              psp.getLogo(),
              psp.getCredential().asString(),
              psp.getTimeout(),
              psp.getBlockVpnAccess(),
              psp.getBlockDataCenterAccess(),
              psp.getFailureRate(),
              psp.getFailureRateThreshold() != null ? psp.getFailureRateThreshold().intValue() : 0,
              psp.getFailureRateDurationMinutes() != null
                  ? psp.getFailureRateDurationMinutes()
                  : 60,
              psp.getIpAddress() != null ? psp.getIpAddress() : new String[0],
              psp.getBrandId(),
              psp.getEnvironmentId(),
              psp.getFlowTargetId(),
              psp.getStatus().getValue(),
              psp.getCreatedAt(),
              psp.getUpdatedAt(),
              psp.getCreatedBy(),
              psp.getUpdatedBy())
          .then(
              Mono.defer(
                  () -> {
                    PspDto responseDto = PspDto.fromEntity(psp);
                    return successResponse(responseDto, "PSP created successfully");
                  }))
          .onErrorResume(
              e -> {
                return databaseError(e, "creating PSP");
              });
    } catch (Exception e) {
      return databaseError(e, "creating PSP");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(String pspId) {
    try {
      validateNotBlank(pspId, "PSP ID");

      return pspRepository
          .findByIdCustom(pspId)
          .flatMap(
              psp -> {
                if (psp == null) {
                  return notFoundError("PSP", "id", pspId);
                }

                // Fetch all related data in parallel
                Mono<List<MaintenanceWindow>> maintenanceWindowsMono =
                    maintenanceWindowRepository.findByPspId(pspId).collectList();

                Mono<List<PspOperation>> operationsMono =
                    pspOperationRepository.findByPspId(pspId).collectList();

                Mono<List<CurrencyLimit>> currencyLimitsMono =
                    currencyLimitRepository.findByPspId(pspId).collectList();

                Mono<PspDetailsDto.FlowTargetInfo> flowTargetMono =
                    flowTargetRepository
                        .findById(psp.getFlowTargetId())
                        .flatMap(this::buildFlowTargetInfo);

                return Mono.zip(
                        maintenanceWindowsMono, operationsMono, currencyLimitsMono, flowTargetMono)
                    .map(
                        tuple -> {
                          List<MaintenanceWindow> maintenanceWindows = tuple.getT1();
                          List<PspOperation> operations = tuple.getT2();
                          List<CurrencyLimit> currencyLimits = tuple.getT3();
                          PspDetailsDto.FlowTargetInfo flowTarget = tuple.getT4();

                          return buildPspDetailsDto(
                              psp, maintenanceWindows, operations, currencyLimits, flowTarget);
                        })
                    .flatMap(dto -> successResponse(dto, "PSP details retrieved successfully"));
              })
          .switchIfEmpty(
              customError(
                  ErrorCode.PSP_NOT_FOUND,
                  "PSP not found with ID: " + pspId,
                  org.springframework.http.HttpStatus.NOT_FOUND));

    } catch (Exception e) {
      return databaseError(e, "retrieving PSP details");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironment(
      String brandId, String environmentId) {
    try {
      validateNotBlank(brandId, "Brand ID");
      validateNotBlank(environmentId, "Environment ID");

      return pspRepository
          .findByBrandIdAndEnvironmentId(brandId, environmentId)
          .map(this::buildPspSummaryDto)
          .collectList()
          .flatMap(pspSummaries -> successResponse(pspSummaries, "PSPs retrieved successfully"));

    } catch (Exception e) {
      return databaseError(e, "retrieving PSPs by brand and environment");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>>
      getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
          String brandId,
          String environmentId,
          String status,
          String currency,
          String flowActionId) {
    try {
      validateNotBlank(brandId, "Brand ID");
      validateNotBlank(environmentId, "Environment ID");
      validateNotBlank(status, "Status");
      validateNotBlank(currency, "Currency");
      validateNotBlank(flowActionId, "Flow Action ID");

      return pspRepository
          .findByBrandIdAndEnvironmentIdAndStatusAndCurrencyAndFlowAction(
              brandId, environmentId, status, currency, flowActionId)
          .map(this::buildPspSummaryDto)
          .collectList()
          .flatMap(pspSummaries -> successResponse(pspSummaries, "PSPs retrieved successfully"));
    } catch (Exception e) {
      return databaseError(
          e, "retrieving PSPs by brand and environment by status, currency and flow action");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getByBrandAndEnvironmentByStatusAndFlowAction(
      String brandId, String environmentId, String status, String flowActionId) {
    try {
      validateNotBlank(brandId, "Brand ID");
      validateNotBlank(environmentId, "Environment ID");
      validateNotBlank(status, "Status");
      validateNotBlank(flowActionId, "Flow Action ID");

      return pspRepository
          .findByBrandIdAndEnvironmentIdAndStatusAndFlowAction(
              brandId, environmentId, status, flowActionId)
          .map(this::buildPspSummaryDto)
          .collectList()
          .flatMap(pspSummaries -> successResponse(pspSummaries, "PSPs retrieved successfully"));
    } catch (Exception e) {
      return databaseError(e, "retrieving PSPs by brand and environment by status and flow action");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getSupportedCurrenciesByBrandAndEnvironment(
      String brandId, String environmentId) {
    try {
      validateNotBlank(brandId, "Brand ID");
      validateNotBlank(environmentId, "Environment ID");

      return pspRepository
          .findSupportedCurrenciesByBrandAndEnvironment(brandId, environmentId)
          .collectList()
          .flatMap(
              currencies ->
                  successResponse(currencies, "Supported currencies retrieved successfully"));
    } catch (Exception e) {
      return databaseError(e, "retrieving supported currencies by brand and environment");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String pspId, UpdatePspDto pspDto) {
    try {
      validateUpdateRequest(pspId, pspDto);

      return operationCurrencyValidationService
          .validateOperationCurrenciesAgainstFlowTarget(convertToValidationRequest(pspDto))
          .flatMap(
              validationResult -> {
                if (!validationResult.isValid()) {
                  return Mono.just(validationResult.getErrorResponse().get());
                }

                Mono<Void> credentialValidationMono =
                    pspDto.getCredential() != null
                        ? validateCredentialAgainstFlowTarget(
                            pspDto.getFlowTargetId(), pspDto.getCredential())
                        : Mono.empty();

                return credentialValidationMono.then(
                    pspRepository
                        .findByIdCustom(pspId)
                        .flatMap(
                            existingPsp -> {
                              if (existingPsp == null) {
                                return customError(
                                    ErrorCode.PSP_NOT_FOUND,
                                    "PSP not found with ID: " + pspId,
                                    org.springframework.http.HttpStatus.NOT_FOUND);
                              } else {
                                return updatePsp(pspId, existingPsp, pspDto);
                              }
                            })
                        .switchIfEmpty(
                            customError(
                                ErrorCode.PSP_NOT_FOUND,
                                "PSP not found with ID: " + pspId,
                                org.springframework.http.HttpStatus.NOT_FOUND)));
              })
          .onErrorResume(
              SchemaValidationException.class,
              e ->
                  customError(
                      ErrorCode.PSP_CONFIGURATION_ERROR,
                      "Invalid credentials: " + e.getMessage(),
                      org.springframework.http.HttpStatus.BAD_REQUEST));

    } catch (Exception e) {
      return databaseError(e, "updating PSP");
    }
  }

  private void validateUpdateRequest(String pspId, UpdatePspDto pspDto) {
    validateNotBlank(pspId, "PSP ID");
    validateNotNull(pspDto, "PSP DTO");

    if (pspDto.getCredential() != null) {
      validateNotBlank(pspDto.getBrandId(), "Brand ID");
      validateNotBlank(pspDto.getEnvironmentId(), "Environment ID");
      validateNotBlank(pspDto.getFlowTargetId(), "Flow target ID");
    }
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updatePsp(
      String pspId, Psp existingPsp, UpdatePspDto pspDto) {
    try {
      Mono<Void> deleteEmptyArraysMono = deleteRecordsForEmptyArrays(pspId, pspDto);

      String encryptedCredential = null;
      if (pspDto.getCredential() != null) {
        encryptedCredential = encryptCredential(pspDto.getCredential());
      }

      String[] ipAddressArray =
          pspDto.getIpAddress() != null
              ? pspDto.getIpAddress().toArray(new String[0])
              : existingPsp.getIpAddress();

      existingPsp.updateBasicDetails(
          pspDto.getName(),
          pspDto.getDescription(),
          pspDto.getLogo(),
          encryptedCredential,
          pspDto.getTimeout(),
          pspDto.getBlockVpnAccess(),
          pspDto.getBlockDataCenterAccess(),
          pspDto.getFailureRate(),
          pspDto.getStatus(),
          pspDto.getFailureRateThreshold(),
          pspDto.getFailureRateDurationMinutes(),
          ipAddressArray,
          pspDto.getUpdatedBy() != null ? pspDto.getUpdatedBy() : "system");

      return deleteEmptyArraysMono
          .then(updatePspInDatabase(pspId, existingPsp))
          .then(updateMaintenanceWindows(pspId, pspDto))
          .then(updateOperations(pspId, pspDto))
          .then(
              Mono.defer(
                  () -> {
                    PspDto responseDto = PspDto.fromEntity(existingPsp);
                    return successResponse(responseDto, "PSP updated successfully");
                  }))
          .onErrorResume(e -> databaseError(e, "updating PSP"));

    } catch (Exception e) {
      return databaseError(e, "updating PSP");
    }
  }

  private Mono<Void> deleteRecordsForEmptyArrays(String pspId, UpdatePspDto pspDto) {
    Mono<Void> deleteMaintenanceWindows = Mono.empty();
    Mono<Void> deleteOperations = Mono.empty();

    if (pspDto.getMaintenanceWindow() != null && pspDto.getMaintenanceWindow().isEmpty()) {
      deleteMaintenanceWindows = maintenanceWindowRepository.deleteByPspId(pspId);
    }

    if (pspDto.getOperations() != null && pspDto.getOperations().isEmpty()) {
      deleteOperations = pspOperationRepository.deleteByPspId(pspId);
    }

    return deleteMaintenanceWindows.then(deleteOperations);
  }

  private Mono<Void> updatePspInDatabase(String pspId, Psp psp) {
    return pspRepository.updatePspWithIpAddress(
        pspId,
        psp.getName(),
        psp.getDescription(),
        psp.getLogo(),
        psp.getCredential().asString(),
        psp.getTimeout(),
        psp.getBlockVpnAccess(),
        psp.getBlockDataCenterAccess(),
        psp.getFailureRate(),
        psp.getFailureRateThreshold() != null ? psp.getFailureRateThreshold().intValue() : 0,
        psp.getFailureRateDurationMinutes(),
        psp.getIpAddress(),
        psp.getStatus().getValue(),
        psp.getUpdatedAt(),
        psp.getUpdatedBy());
  }

  private Mono<Void> updateMaintenanceWindows(String pspId, UpdatePspDto pspDto) {
    if (pspDto.getMaintenanceWindow() == null) {
      return Mono.empty();
    }

    if (!pspDto.getMaintenanceWindow().isEmpty()) {
      return maintenanceWindowRepository
          .deleteByPspId(pspId)
          .then(
              Flux.fromIterable(pspDto.getMaintenanceWindow())
                  .flatMap(
                      window -> {
                        MaintenanceWindow maintenanceWindow =
                            MaintenanceWindow.create(
                                pspId,
                                window.getFlowActionId(),
                                LocalDateTime.parse(window.getStartAt()),
                                LocalDateTime.parse(window.getEndAt()),
                                pspDto.getUpdatedBy() != null ? pspDto.getUpdatedBy() : "system");
                        return maintenanceWindowRepository.insertMaintenanceWindow(
                            maintenanceWindow.getId(),
                            maintenanceWindow.getPspId(),
                            maintenanceWindow.getFlowActionId(),
                            maintenanceWindow.getStartAt(),
                            maintenanceWindow.getEndAt(),
                            maintenanceWindow.getStatus().getValue(),
                            maintenanceWindow.getCreatedAt(),
                            maintenanceWindow.getUpdatedAt(),
                            maintenanceWindow.getCreatedBy(),
                            maintenanceWindow.getUpdatedBy());
                      })
                  .then());
    }

    return Mono.empty();
  }

  private Mono<Void> updateOperations(String pspId, UpdatePspDto pspDto) {
    if (pspDto.getOperations() == null) {
      return Mono.empty();
    }

    if (!pspDto.getOperations().isEmpty()) {
      return pspOperationRepository
          .deleteByPspId(pspId)
          .then(
              Flux.fromIterable(pspDto.getOperations())
                  .flatMap(
                      operation -> {
                        PspOperation pspOperation =
                            PspOperation.create(
                                pspDto.getBrandId(),
                                pspDto.getEnvironmentId(),
                                pspId,
                                operation.getFlowActionId(),
                                operation.getFlowDefinitionId());
                        pspOperation.updateStatus(operation.getStatus());
                        return pspOperationRepository
                            .insertPspOperation(
                                pspDto.getBrandId(),
                                pspDto.getEnvironmentId(),
                                pspId,
                                operation.getFlowActionId(),
                                operation.getFlowDefinitionId(),
                                operation.getStatus())
                            .then(updateCurrencies(pspId, pspDto, operation));
                      })
                  .then());
    }

    return Mono.empty();
  }

  private Mono<Void> updateCurrencies(
      String pspId, UpdatePspDto pspDto, UpdatePspDto.PspOperationDto operation) {
    if (operation.getCurrencies() == null) {
      return Mono.empty();
    }

    if (operation.getCurrencies().isEmpty()) {
      return currencyLimitRepository.deleteByCompositeKey(
          pspDto.getBrandId(), pspDto.getEnvironmentId(), operation.getFlowActionId(), pspId);
    }

    return currencyLimitRepository
        .deleteByCompositeKey(
            pspDto.getBrandId(), pspDto.getEnvironmentId(), operation.getFlowActionId(), pspId)
        .then(
            Flux.fromIterable(operation.getCurrencies())
                .flatMap(
                    currency -> {
                      return currencyLimitRepository.insertCurrencyLimit(
                          pspDto.getBrandId(),
                          pspDto.getEnvironmentId(),
                          operation.getFlowActionId(),
                          pspId,
                          currency.getCurrency(),
                          currency.getMinValue(),
                          currency.getMaxValue());
                    })
                .then());
  }

  private Mono<PspDetailsDto.FlowTargetInfo> buildFlowTargetInfo(FlowTarget flowTarget) {
    if (flowTarget == null) {
      return Mono.just(null);
    }

    return flowDefinitionRepository
        .findByFlowTargetId(flowTarget.getId())
        .flatMap(
            flowDefinition ->
                flowActionRepository
                    .findById(flowDefinition.getFlowActionId())
                    .map(
                        flowAction ->
                            PspDetailsDto.SupportedActionInfo.builder()
                                .flowActionId(flowDefinition.getFlowActionId())
                                .flowDefinitionId(flowDefinition.getId())
                                .flowActionName(flowAction != null ? flowAction.getName() : null)
                                .build()))
        .collectList()
        .map(
            supportedActions ->
                PspDetailsDto.FlowTargetInfo.builder()
                    .id(flowTarget.getId())
                    .credentialSchema(flowTarget.getCredentialSchema().asString())
                    .countries(flowTarget.getCountries())
                    .paymentMethods(flowTarget.getPaymentMethods())
                    .flowTypeId(flowTarget.getFlowTypeId())
                    .currencies(flowTarget.getCurrencies())
                    .supportedActions(supportedActions)
                    .build());
  }

  private PspDetailsDto buildPspDetailsDto(
      Psp psp,
      List<MaintenanceWindow> maintenanceWindows,
      List<PspOperation> operations,
      List<CurrencyLimit> currencyLimits,
      PspDetailsDto.FlowTargetInfo flowTarget) {

    Map<String, List<CurrencyLimit>> currencyLimitsByOperation =
        currencyLimits.stream()
            .collect(
                Collectors.groupingBy(
                    limit ->
                        limit.getBrandId()
                            + ":"
                            + limit.getEnvironmentId()
                            + ":"
                            + limit.getFlowActionId()
                            + ":"
                            + limit.getPspId()));

    List<PspDetailsDto.MaintenanceWindowDto> maintenanceWindowDtos =
        maintenanceWindows.stream()
            .map(
                window ->
                    PspDetailsDto.MaintenanceWindowDto.builder()
                        .id(window.getId())
                        .flowActionId(window.getFlowActionId())
                        .startAt(window.getStartAt())
                        .endAt(window.getEndAt())
                        .build())
            .collect(Collectors.toList());

    List<PspDetailsDto.PspOperationDto> operationDtos =
        operations.stream()
            .map(
                operation -> {
                  String operationKey =
                      operation.getBrandId()
                          + ":"
                          + operation.getEnvironmentId()
                          + ":"
                          + operation.getFlowActionId()
                          + ":"
                          + operation.getPspId();

                  List<PspDetailsDto.CurrencyDto> operationCurrencies =
                      currencyLimitsByOperation.getOrDefault(operationKey, List.of()).stream()
                          .map(
                              limit ->
                                  PspDetailsDto.CurrencyDto.builder()
                                      .currency(limit.getCurrency())
                                      .minValue(limit.getMinValue())
                                      .maxValue(limit.getMaxValue())
                                      .build())
                          .collect(Collectors.toList());

                  return PspDetailsDto.PspOperationDto.builder()
                      .flowActionId(operation.getFlowActionId())
                      .flowDefinitionId(operation.getFlowDefinitionId())
                      .status(operation.getStatus())
                      .currencies(operationCurrencies)
                      .build();
                })
            .collect(Collectors.toList());

    return PspDetailsDto.builder()
        .id(psp.getId())
        .name(psp.getName())
        .description(psp.getDescription())
        .logo(psp.getLogo())
        .credential("***ENCRYPTED***")
        .timeout(psp.getTimeout())
        .blockVpnAccess(psp.getBlockVpnAccess())
        .blockDataCenterAccess(psp.getBlockDataCenterAccess())
        .failureRate(psp.getFailureRate())
        .failureRateThreshold(psp.getFailureRateThreshold())
        .failureRateDurationMinutes(psp.getFailureRateDurationMinutes())
        .brandId(psp.getBrandId())
        .environmentId(psp.getEnvironmentId())
        .flowTargetId(psp.getFlowTargetId())
        .status(psp.getStatus())
        .createdAt(psp.getCreatedAt())
        .updatedAt(psp.getUpdatedAt())
        .createdBy(psp.getCreatedBy())
        .updatedBy(psp.getUpdatedBy())
        .ipAddress(List.of(psp.getIpAddress()))
        .maintenanceWindow(maintenanceWindowDtos)
        .operations(operationDtos)
        .flowTarget(flowTarget)
        .build();
  }

  private PspSummaryDto buildPspSummaryDto(Psp psp) {
    return PspSummaryDto.builder()
        .id(psp.getId())
        .name(psp.getName())
        .description(psp.getDescription())
        .logo(psp.getLogo())
        .status(psp.getStatus())
        .createdAt(psp.getCreatedAt())
        .updatedAt(psp.getUpdatedAt())
        .createdBy(psp.getCreatedBy())
        .updatedBy(psp.getUpdatedBy())
        .build();
  }

  private OperationCurrencyValidationRequest convertToValidationRequest(UpdatePspDto pspDto) {
    List<OperationCurrencyValidationRequest.PspOperation> operations = new java.util.ArrayList<>();

    if (pspDto.getOperations() != null) {
      for (UpdatePspDto.PspOperationDto operation : pspDto.getOperations()) {
        List<OperationCurrencyValidationRequest.CurrencyInfo> currencies =
            new java.util.ArrayList<>();

        if (operation.getCurrencies() != null) {
          for (UpdatePspDto.CurrencyDto currency : operation.getCurrencies()) {
            currencies.add(
                OperationCurrencyValidationRequest.CurrencyInfo.builder()
                    .currency(currency.getCurrency())
                    .minValue(
                        currency.getMinValue() != null ? currency.getMinValue().toString() : null)
                    .maxValue(
                        currency.getMaxValue() != null ? currency.getMaxValue().toString() : null)
                    .build());
          }
        }

        operations.add(
            OperationCurrencyValidationRequest.PspOperation.builder()
                .flowActionId(operation.getFlowActionId())
                .flowDefinitionId(operation.getFlowDefinitionId())
                .currencies(currencies)
                .build());
      }
    }

    return OperationCurrencyValidationRequest.builder()
        .flowTargetId(pspDto.getFlowTargetId())
        .operations(operations)
        .build();
  }
}
