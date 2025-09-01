package nexxus.riskrule.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowaction.repository.FlowActionRepository;
import nexxus.psp.repository.PspRepository;
import nexxus.riskrule.dto.RiskRuleDetailedDto;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.riskrule.dto.RiskRulePspDto;
import nexxus.riskrule.entity.RiskRule;
import nexxus.riskrule.entity.RiskRulePsp;
import nexxus.riskrule.repository.RiskRulePspRepository;
import nexxus.riskrule.repository.RiskRuleRepository;
import nexxus.riskrule.service.RiskRuleService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.RiskType;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.CurrencyValidationService;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiskRuleServiceImpl implements RiskRuleService {

  private final RiskRuleRepository riskRuleRepository;
  private final RiskRulePspRepository riskRulePspRepository;
  private final FlowActionRepository flowActionRepository;
  private final PspRepository pspRepository;
  private final CurrencyValidationService currencyValidationService;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(RiskRuleDto dto) {
    try {
      if (dto.getPsps() == null || dto.getPsps().isEmpty()) {
        return customError(
            ErrorCode.RISK_RULE_INVALID,
            "PSPs list must contain at least one item",
            org.springframework.http.HttpStatus.BAD_REQUEST);
      }

      Mono<ResponseEntity<ApiResponse<Object>>> validationOutcome = validateCustomerCriteria(dto);
      if (validationOutcome != null) {
        return validationOutcome;
      }

      // Validate currency support with PSPs and Flow Target fallback
      List<String> pspIds =
          dto.getPsps().stream().map(RiskRulePspDto::getId).collect(Collectors.toList());

      return currencyValidationService
          .validateCurrencySupportWithFlowTargetFallback(
              dto.getCurrency(),
              pspIds,
              dto.getBrandId(),
              dto.getEnvironmentId(),
              dto.getFlowActionId())
          .flatMap(
              currencyValidationOutcome -> {
                if (!currencyValidationOutcome.isValid()) {
                  // Validation failed, return the error response
                  return Mono.just(currencyValidationOutcome.getErrorResponse().get());
                }

                // Validation passed, continue with risk rule creation
                RiskRule riskRule =
                    RiskRule.create(
                        dto.getName(),
                        dto.getType(),
                        dto.getAction(),
                        dto.getCurrency(),
                        dto.getDuration(),
                        dto.getMaxAmount(),
                        dto.getBrandId(),
                        dto.getEnvironmentId(),
                        dto.getFlowActionId(),
                        dto.getCriteriaType(),
                        dto.getCriteriaValue());

                return riskRuleRepository
                    .insertRiskRule(
                        riskRule.getId(),
                        riskRule.getVersion(),
                        riskRule.getName(),
                        riskRule.getType().name(),
                        riskRule.getAction().name(),
                        riskRule.getCurrency(),
                        riskRule.getDuration().name(),
                        riskRule.getCriteriaType() != null
                            ? riskRule.getCriteriaType().name()
                            : null,
                        riskRule.getCriteriaValue(),
                        riskRule.getMaxAmount(),
                        riskRule.getBrandId(),
                        riskRule.getEnvironmentId(),
                        riskRule.getFlowActionId(),
                        riskRule.getStatus().name(),
                        riskRule.getCreatedAt(),
                        riskRule.getUpdatedAt(),
                        riskRule.getCreatedBy(),
                        riskRule.getUpdatedBy())
                    .then(Mono.just(riskRule))
                    .flatMap(
                        savedRule -> {
                          List<RiskRulePsp> psps =
                              dto.getPsps().stream()
                                  .map(
                                      pspDto ->
                                          RiskRulePsp.create(
                                              savedRule.getId(),
                                              savedRule.getVersion(),
                                              pspDto.getId()))
                                  .toList();

                          return Flux.fromIterable(psps)
                              .flatMap(
                                  psp ->
                                      riskRulePspRepository.insertRiskRulePsp(
                                          psp.getRiskRuleId(),
                                          psp.getRiskRuleVersion(),
                                          psp.getPspId()))
                              .collectList()
                              .then(
                                  buildDetailedRiskRuleResponse(
                                          savedRule,
                                          dto.getPsps().stream()
                                              .map(
                                                  pspDto ->
                                                      RiskRulePsp.create(
                                                          savedRule.getId(),
                                                          savedRule.getVersion(),
                                                          pspDto.getId()))
                                              .collect(Collectors.toList()))
                                      .map(
                                          responseDto ->
                                              responseHandler.successResponse(
                                                  responseDto, "Risk Rule created successfully")));
                        })
                    .onErrorResume(e -> databaseError(e, "creating Risk Rule"));
              });

    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findById(String id) {
    try {
      return riskRuleRepository
          .findLatestById(id)
          .flatMap(
              riskRule -> {
                return riskRulePspRepository
                    .findByRiskRuleIdAndRiskRuleVersion(riskRule.getId(), riskRule.getVersion())
                    .collectList()
                    .flatMap(psps -> buildDetailedRiskRuleResponse(riskRule, psps))
                    .map(
                        responseDto ->
                            responseHandler.successResponse(
                                responseDto, "Risk Rule found successfully"));
              })
          .switchIfEmpty(
              Mono.just(
                  responseHandler.errorResponse(
                      ErrorCode.RISK_RULE_NOT_FOUND,
                      "Risk Rule not found with ID: " + id,
                      org.springframework.http.HttpStatus.NOT_FOUND)))
          .onErrorResume(e -> databaseError(e, "finding Risk Rule"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findAllByBrandIdAndEnvironmentId(
      String brandId, String environmentId) {
    try {
      return riskRuleRepository
          .findLatestByBrandIdAndEnvironmentId(brandId, environmentId)
          .flatMap(
              riskRule -> {
                return riskRulePspRepository
                    .findByRiskRuleIdAndRiskRuleVersion(riskRule.getId(), riskRule.getVersion())
                    .collectList()
                    .flatMap(psps -> buildDetailedRiskRuleResponse(riskRule, psps));
              })
          .collectList()
          .map(
              riskRules ->
                  responseHandler.successResponse(riskRules, "Risk Rules found successfully"))
          .onErrorResume(e -> databaseError(e, "finding Risk Rules"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findAllByPspId(String pspId) {
    try {
      return riskRulePspRepository
          .findRiskRuleIdsByPspId(pspId)
          .flatMap(riskRuleId -> riskRuleRepository.findLatestById(riskRuleId))
          .flatMap(
              riskRule -> {
                return riskRulePspRepository
                    .findByRiskRuleIdAndRiskRuleVersion(riskRule.getId(), riskRule.getVersion())
                    .collectList()
                    .flatMap(psps -> buildDetailedRiskRuleResponse(riskRule, psps));
              })
          .collectList()
          .map(
              riskRules ->
                  responseHandler.successResponse(riskRules, "Risk Rules found successfully"))
          .onErrorResume(e -> databaseError(e, "finding Risk Rules"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> deleteById(String id) {
    try {
      return riskRuleRepository
          .findLatestById(id)
          .flatMap(
              existingRule -> {
                return riskRulePspRepository
                    .deleteAllByRiskRuleId(id)
                    .then(riskRuleRepository.deleteAllById(id))
                    .then(
                        Mono.just(
                            responseHandler.successResponse(
                                null, "Risk Rule and all versions deleted successfully")));
              })
          .switchIfEmpty(
              Mono.just(
                  responseHandler.errorResponse(
                      ErrorCode.RISK_RULE_NOT_FOUND,
                      "Risk Rule not found with ID: " + id,
                      org.springframework.http.HttpStatus.NOT_FOUND)))
          .onErrorResume(e -> databaseError(e, "deleting Risk Rule"));
    } catch (Exception e) {
      return databaseError(e);
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, RiskRuleDto dto) {
    try {
      if (dto.getPsps() == null || dto.getPsps().isEmpty()) {
        return customError(
            ErrorCode.RISK_RULE_INVALID,
            "PSPs list must contain at least one item",
            org.springframework.http.HttpStatus.BAD_REQUEST);
      }

      Mono<ResponseEntity<ApiResponse<Object>>> validationOutcome = validateCustomerCriteria(dto);
      if (validationOutcome != null) {
        return validationOutcome;
      }

      // Validate currency support with PSPs and Flow Target fallback
      List<String> pspIds =
          dto.getPsps().stream().map(RiskRulePspDto::getId).collect(Collectors.toList());

      return currencyValidationService
          .validateCurrencySupportWithFlowTargetFallback(
              dto.getCurrency(),
              pspIds,
              dto.getBrandId(),
              dto.getEnvironmentId(),
              dto.getFlowActionId())
          .flatMap(
              currencyValidationOutcome -> {
                if (!currencyValidationOutcome.isValid()) {
                  // Validation failed, return the error response
                  return Mono.just(currencyValidationOutcome.getErrorResponse().get());
                }

                return riskRuleRepository
                    .findLatestById(id)
                    .flatMap(
                        existingRule -> {
                          try {
                            Integer newVersion = existingRule.getVersion() + 1;

                            RiskRule newRule =
                                RiskRule.builder()
                                    .riskRuleId(
                                        new nexxus.riskrule.entity.RiskRuleId(
                                            existingRule.getId(), newVersion))
                                    .id(existingRule.getId())
                                    .version(newVersion)
                                    .name(dto.getName())
                                    .type(dto.getType())
                                    .action(dto.getAction())
                                    .currency(dto.getCurrency())
                                    .duration(dto.getDuration())
                                    .criteriaType(dto.getCriteriaType())
                                    .criteriaValue(dto.getCriteriaValue())
                                    .maxAmount(dto.getMaxAmount())
                                    .brandId(dto.getBrandId())
                                    .environmentId(dto.getEnvironmentId())
                                    .flowActionId(dto.getFlowActionId())
                                    .status(
                                        dto.getStatus() != null
                                            ? dto.getStatus()
                                            : existingRule.getStatus())
                                    .createdAt(existingRule.getCreatedAt())
                                    .updatedAt(java.time.LocalDateTime.now())
                                    .createdBy(existingRule.getCreatedBy())
                                    .updatedBy(
                                        dto.getUpdatedBy() != null ? dto.getUpdatedBy() : "system")
                                    .build();

                            return riskRuleRepository
                                .insertRiskRule(
                                    newRule.getId(),
                                    newRule.getVersion(),
                                    newRule.getName(),
                                    newRule.getType().name(),
                                    newRule.getAction().name(),
                                    newRule.getCurrency(),
                                    newRule.getDuration().name(),
                                    newRule.getCriteriaType() != null
                                        ? newRule.getCriteriaType().name()
                                        : null,
                                    newRule.getCriteriaValue(),
                                    newRule.getMaxAmount(),
                                    newRule.getBrandId(),
                                    newRule.getEnvironmentId(),
                                    newRule.getFlowActionId(),
                                    newRule.getStatus().name(),
                                    newRule.getCreatedAt(),
                                    newRule.getUpdatedAt(),
                                    newRule.getCreatedBy(),
                                    newRule.getUpdatedBy())
                                .then(Mono.just(newRule))
                                .flatMap(
                                    savedRule -> {
                                      List<RiskRulePsp> psps =
                                          dto.getPsps().stream()
                                              .map(
                                                  pspDto ->
                                                      RiskRulePsp.create(
                                                          savedRule.getId(),
                                                          savedRule.getVersion(),
                                                          pspDto.getId()))
                                              .toList();

                                      return Flux.fromIterable(psps)
                                          .flatMap(
                                              psp ->
                                                  riskRulePspRepository.insertRiskRulePsp(
                                                      psp.getRiskRuleId(),
                                                      psp.getRiskRuleVersion(),
                                                      psp.getPspId()))
                                          .collectList()
                                          .then(
                                              buildDetailedRiskRuleResponse(
                                                      savedRule,
                                                      dto.getPsps().stream()
                                                          .map(
                                                              pspDto ->
                                                                  RiskRulePsp.create(
                                                                      savedRule.getId(),
                                                                      savedRule.getVersion(),
                                                                      pspDto.getId()))
                                                          .collect(Collectors.toList()))
                                                  .map(
                                                      responseDto ->
                                                          responseHandler.successResponse(
                                                              responseDto,
                                                              "Risk Rule version "
                                                                  + newVersion
                                                                  + " created successfully")));
                                    });
                          } catch (Exception e) {
                            return databaseError(e, "creating new Risk Rule version");
                          }
                        })
                    .switchIfEmpty(
                        Mono.just(
                            responseHandler.errorResponse(
                                ErrorCode.RISK_RULE_NOT_FOUND,
                                "Risk Rule not found with ID: " + id,
                                org.springframework.http.HttpStatus.NOT_FOUND)))
                    .onErrorResume(e -> databaseError(e));
              });

    } catch (Exception e) {
      return databaseError(e);
    }
  }

  private Mono<RiskRuleDetailedDto> buildDetailedRiskRuleResponse(
      RiskRule riskRule, List<RiskRulePsp> psps) {
    return Mono.zip(
            flowActionRepository
                .findById(riskRule.getFlowActionId())
                .map(flowAction -> flowAction.getName()),
            Flux.fromIterable(psps)
                .flatMap(
                    riskRulePsp ->
                        pspRepository
                            .findById(riskRulePsp.getPspId())
                            .map(
                                psp ->
                                    RiskRuleDetailedDto.RiskRuleDetailedPspDto.builder()
                                        .id(riskRulePsp.getPspId())
                                        .name(psp.getName())
                                        .build()))
                .collectList())
        .map(
            tuple -> {
              String flowActionName = tuple.getT1();
              List<RiskRuleDetailedDto.RiskRuleDetailedPspDto> pspDtos = tuple.getT2();

              RiskRuleDetailedDto baseDto = RiskRuleDetailedDto.fromEntity(riskRule);

              baseDto.setFlowActionName(flowActionName);
              baseDto.setPsps(pspDtos);
              return baseDto;
            });
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> validateCustomerCriteria(RiskRuleDto dto) {
    if (RiskType.CUSTOMER.equals(dto.getType())) {
      if (dto.getCriteriaType() == null) {
        return customError(
            ErrorCode.RISK_RULE_INVALID,
            "criteriaType is required when type is CUSTOMER",
            org.springframework.http.HttpStatus.BAD_REQUEST);
      }
      if (dto.getCriteriaValue() == null || dto.getCriteriaValue().trim().isEmpty()) {
        return customError(
            ErrorCode.RISK_RULE_INVALID,
            "criteriaValue is required when type is CUSTOMER",
            org.springframework.http.HttpStatus.BAD_REQUEST);
      }
    } else if (RiskType.DEFAULT.equals(dto.getType())) {
      boolean hasCriteriaFields =
          dto.getCriteriaType() != null
              || (dto.getCriteriaValue() != null && !dto.getCriteriaValue().trim().isEmpty());

      if (hasCriteriaFields) {
        return customError(
            ErrorCode.RISK_RULE_INVALID,
            "criteriaType and criteriaValue must not be provided when type is DEFAULT",
            org.springframework.http.HttpStatus.BAD_REQUEST);
      }
    }
    return null;
  }
}
