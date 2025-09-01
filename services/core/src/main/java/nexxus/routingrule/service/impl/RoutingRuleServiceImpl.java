package nexxus.routingrule.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.routingrule.dto.RoutingRulePspDto;
import nexxus.routingrule.dto.UpdateRoutingRuleDto;
import nexxus.routingrule.entity.RoutingRule;
import nexxus.routingrule.entity.RoutingRulePsp;
import nexxus.routingrule.repository.RoutingRulePspRepository;
import nexxus.routingrule.repository.RoutingRuleRepository;
import nexxus.routingrule.service.RoutingRuleService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoutingRuleServiceImpl implements RoutingRuleService {

  private final RoutingRuleRepository routingRuleRepository;
  private final RoutingRulePspRepository pspRepository;
  private final ReactiveResponseHandler responseHandler;
  private final ObjectMapper objectMapper;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(RoutingRuleDto routingRuleDto) {
    return routingRuleRepository
        .countByBrandIdAndEnvironmentId(
            routingRuleDto.getBrandId(), routingRuleDto.getEnvironmentId())
        .flatMap(
            count -> {
              Boolean isDefault = count == 0;

              try {
                String conditionJsonString =
                    objectMapper.writeValueAsString(routingRuleDto.getConditionJson());

                RoutingRule routingRule =
                    RoutingRule.create(
                        routingRuleDto.getName(),
                        routingRuleDto.getBrandId(),
                        routingRuleDto.getEnvironmentId(),
                        routingRuleDto.getPspSelectionMode(),
                        conditionJsonString,
                        isDefault,
                        routingRuleDto.getCreatedBy());

                return routingRuleRepository
                    .insertRoutingRule(
                        routingRule.getId(),
                        routingRule.getVersion(),
                        routingRule.getName(),
                        routingRule.getBrandId(),
                        routingRule.getEnvironmentId(),
                        routingRule.getPspSelectionMode().name(),
                        conditionJsonString,
                        routingRule.getIsDefault(),
                        routingRule.getStatus().name(),
                        routingRule.getCreatedAt(),
                        routingRule.getUpdatedAt(),
                        routingRule.getCreatedBy(),
                        routingRule.getUpdatedBy())
                    .then(Mono.just(routingRule));
              } catch (Exception e) {
                return Mono.error(e);
              }
            })
        .flatMap(
            routingRule -> {
              return createPsps(
                      routingRule.getId(), routingRule.getVersion(), routingRuleDto.getPsps())
                  .then(
                      Mono.defer(
                          () -> {
                            RoutingRuleDto responseDto = RoutingRuleDto.fromEntity(routingRule);
                            return successResponse(
                                responseDto, "Routing rule created successfully");
                          }));
            })
        .onErrorResume(e -> databaseError(e, "creating routing rule"));
  }

  private Mono<Void> createPsps(
      String routingRuleId, Integer version, List<RoutingRulePspDto> psps) {
    return Flux.fromIterable(psps)
        .flatMap(
            pspDto -> {
              RoutingRulePsp psp =
                  RoutingRulePsp.create(
                      routingRuleId, version, pspDto.getPspId(), pspDto.getPspValue());

              return pspRepository.insertPsp(
                  psp.getRoutingRuleId(),
                  psp.getRoutingRuleVersion(),
                  psp.getPspId(),
                  psp.getPspValue());
            })
        .then();
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    return routingRuleRepository
        .findLatestVersionById(id)
        .flatMap(
            routingRule -> {
              if (routingRule.getIsDefault()) {
                return Mono.just(
                    responseHandler.errorResponse(
                        ErrorCode.ROUTING_DEFAULT_RULE_DELETE_FORBIDDEN, HttpStatus.BAD_REQUEST));
              }

              return routingRuleRepository
                  .countByBrandIdAndEnvironmentId(
                      routingRule.getBrandId(), routingRule.getEnvironmentId())
                  .flatMap(
                      count -> {
                        if (count <= 1) {
                          return Mono.just(
                              responseHandler.errorResponse(
                                  ErrorCode.ROUTING_LAST_RULE_DELETE_FORBIDDEN,
                                  HttpStatus.BAD_REQUEST));
                        }

                        Mono<Integer> deletePsps = pspRepository.deleteAllByRoutingRuleId(id);

                        Mono<Integer> deleteRoutingRule = routingRuleRepository.deleteAllById(id);

                        return deletePsps
                            .then(deleteRoutingRule)
                            .then(
                                successResponse(
                                    null,
                                    "Routing rule and all its versions deleted successfully"));
                      });
            })
        .switchIfEmpty(
            customError(
                ErrorCode.ROUTING_RULE_NOT_FOUND,
                "Routing rule not found with ID: " + id,
                org.springframework.http.HttpStatus.NOT_FOUND))
        .onErrorResume(e -> databaseError(e, "deleting routing rule"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(String id) {
    return routingRuleRepository
        .findLatestVersionById(id)
        .flatMap(this::buildRoutingRuleWithAssociations)
        .flatMap(routingRule -> successResponse(routingRule, "Routing rule retrieved successfully"))
        .switchIfEmpty(
            customError(
                ErrorCode.ROUTING_RULE_NOT_FOUND,
                "Routing rule not found with ID: " + id,
                org.springframework.http.HttpStatus.NOT_FOUND))
        .onErrorResume(e -> databaseError(e, "retrieving routing rule"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      String id, UpdateRoutingRuleDto updateRoutingRuleDto) {
    return routingRuleRepository
        .findLatestVersionById(id)
        .flatMap(
            existingRoutingRule -> {
              Integer newVersion = existingRoutingRule.getVersion() + 1;

              try {
                String conditionJsonString =
                    updateRoutingRuleDto.getConditionJson() != null
                        ? objectMapper.writeValueAsString(updateRoutingRuleDto.getConditionJson())
                        : existingRoutingRule.getConditionJson().asString();

                Boolean shouldBeDefault =
                    updateRoutingRuleDto.getIsDefault() != null
                        ? updateRoutingRuleDto.getIsDefault()
                        : existingRoutingRule.getIsDefault();

                RoutingRule updatedRoutingRule =
                    RoutingRule.createNewVersion(
                        existingRoutingRule,
                        updateRoutingRuleDto.getName(),
                        existingRoutingRule.getBrandId(),
                        existingRoutingRule.getEnvironmentId(),
                        updateRoutingRuleDto.getPspSelectionMode() != null
                            ? updateRoutingRuleDto.getPspSelectionMode()
                            : existingRoutingRule.getPspSelectionMode(),
                        conditionJsonString,
                        shouldBeDefault,
                        newVersion,
                        updateRoutingRuleDto.getUpdatedBy() != null
                            ? updateRoutingRuleDto.getUpdatedBy()
                            : "system");

                updatedRoutingRule.setStatus(
                    updateRoutingRuleDto.getStatus() != null
                        ? updateRoutingRuleDto.getStatus()
                        : existingRoutingRule.getStatus());

                return routingRuleRepository
                    .insertRoutingRule(
                        updatedRoutingRule.getId(),
                        updatedRoutingRule.getVersion(),
                        updatedRoutingRule.getName(),
                        updatedRoutingRule.getBrandId(),
                        updatedRoutingRule.getEnvironmentId(),
                        updatedRoutingRule.getPspSelectionMode().name(),
                        conditionJsonString,
                        updatedRoutingRule.getIsDefault(),
                        updatedRoutingRule.getStatus().name(),
                        updatedRoutingRule.getCreatedAt(),
                        updatedRoutingRule.getUpdatedAt(),
                        updatedRoutingRule.getCreatedBy(),
                        updatedRoutingRule.getUpdatedBy())
                    .then(Mono.just(updatedRoutingRule));
              } catch (Exception e) {
                return Mono.error(e);
              }
            })
        .flatMap(
            updatedRoutingRule -> {
              // If this routing rule is being set as default, update all other routing rules to
              // non-default
              Mono<Void> updateOtherRules = Mono.empty();
              if (updatedRoutingRule.getIsDefault()) {
                updateOtherRules =
                    updateOtherRulesToNonDefault(
                        updatedRoutingRule.getId(),
                        updatedRoutingRule.getBrandId(),
                        updatedRoutingRule.getEnvironmentId());
              }

              return updateOtherRules
                  .then(
                      createPsps(
                          updatedRoutingRule.getId(),
                          updatedRoutingRule.getVersion(),
                          updateRoutingRuleDto.getPsps()))
                  .then(
                      Mono.defer(
                          () -> {
                            RoutingRuleDto responseDto =
                                RoutingRuleDto.fromEntity(updatedRoutingRule);
                            return successResponse(
                                responseDto,
                                "Routing rule version "
                                    + updatedRoutingRule.getVersion()
                                    + " created successfully");
                          }));
            })
        .switchIfEmpty(
            customError(
                ErrorCode.ROUTING_RULE_NOT_FOUND,
                "Routing rule not found with ID: " + id,
                org.springframework.http.HttpStatus.NOT_FOUND))
        .onErrorResume(e -> databaseError(e, "updating routing rule"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAllByBrandAndEnvironment(
      String brandId, String environmentId) {
    return routingRuleRepository
        .findByBrandIdAndEnvironmentId(brandId, environmentId)
        .flatMap(this::buildRoutingRuleWithAssociations)
        .collectList()
        .flatMap(
            routingRules -> successResponse(routingRules, "Routing rules retrieved successfully"))
        .onErrorResume(e -> databaseError(e, "retrieving routing rules"));
  }

  private Mono<RoutingRuleDto> buildRoutingRuleWithAssociations(RoutingRule routingRule) {
    return pspRepository
        .findByRoutingRuleIdAndRoutingRuleVersion(routingRule.getId(), routingRule.getVersion())
        .collectList()
        .map(psps -> RoutingRuleDto.fromEntityWithAssociations(routingRule, psps));
  }

  private Mono<Void> updateOtherRulesToNonDefault(
      String currentRuleId, String brandId, String environmentId) {
    return routingRuleRepository
        .updateOtherRulesToNonDefault(brandId, environmentId, currentRuleId)
        .then();
  }
}
