package nexxus.flowtarget.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowaction.repository.FlowActionRepository;
import nexxus.flowdefinition.repository.FlowDefinitionRepository;
import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.flowtarget.entity.FlowTarget;
import nexxus.flowtarget.repository.FlowTargetRepository;
import nexxus.flowtarget.service.FlowTargetService;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.ResourceNotFoundException;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.util.ReactiveResponseHandler;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlowTargetServiceImpl implements FlowTargetService {

  private final FlowTargetRepository flowTargetRepository;
  private final FlowDefinitionRepository flowDefinitionRepository;
  private final FlowActionRepository flowActionRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(String flowTypeId, FlowTargetDto dto) {
    try {
      validateCreateRequest(dto);
      validateNotBlank(flowTypeId, "Flow type ID");

      return flowTargetRepository
          .findByFlowTypeId(flowTypeId)
          .filter(existing -> existing.getName().equals(dto.getName()))
          .hasElements()
          .flatMap(
              exists -> {
                if (exists) {
                  return conflictError("Flow target already exists with name: " + dto.getName());
                } else {
                  return createFlowTarget(flowTypeId, dto);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "creating flow target");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll(String flowTypeId) {
    validateNotBlank(flowTypeId, "Flow type ID");

    return flowTargetRepository
        .findByFlowTypeId(flowTypeId)
        .collectList()
        .flatMap(
            flowTargets -> {
              List<Mono<FlowTargetDto>> flowTargetMonos =
                  flowTargets.stream()
                      .map(this::buildFlowTargetWithAssociations)
                      .collect(Collectors.toList());

              return Flux.fromIterable(flowTargetMonos)
                  .flatMap(mono -> mono)
                  .collectList()
                  .flatMap(this::successResponse);
            })
        .onErrorResume(e -> databaseError(e, "retrieving flow targets"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> read(String id) {
    validateNotBlank(id, "Flow target ID");

    return flowTargetRepository
        .findById(id)
        .flatMap(
            flowTarget ->
                Mono.just(FlowTargetDto.fromEntity(flowTarget)).flatMap(this::successResponse))
        .switchIfEmpty(notFoundError("Flow target", "id", id))
        .onErrorResume(e -> databaseError(e, "retrieving flow target"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(
      String flowTypeId, String id, FlowTargetDto dto) {
    try {
      validateNotBlank(id, "Flow target ID");
      validateNotBlank(flowTypeId, "Flow type ID");
      validateUpdateRequest(dto);

      return flowTargetRepository
          .findById(id)
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return updateFlowTarget(flowTypeId, id, dto);
                } else {
                  return notFoundError("Flow target", "id", id);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "updating flow target");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    validateNotBlank(id, "Flow target ID");

    return flowTargetRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return flowTargetRepository
                    .deleteById(id)
                    .then(successResponse("Flow target deleted successfully"));
              } else {
                return notFoundError("Flow target", "id", id);
              }
            })
        .onErrorResume(
            e -> {
              String errorMessage = e.getMessage();
              if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                return conflictError(
                    "Cannot delete flow target: It has associated data. Please remove the associated data first.");
              }
              return databaseError("Error deleting flow target: " + errorMessage);
            });
  }

  private void validateCreateRequest(FlowTargetDto dto) {
    validateNotNull(dto, "Flow target DTO");
    validateNotBlank(dto.getName(), "Flow target name");
    validateNotBlank(dto.getLogo(), "Flow target logo");
    validateNotNull(dto.getStatus(), "Flow target status");
    validateNotBlank(dto.getCredentialSchema(), "Credential schema");
    validateNotBlank(dto.getFlowTypeId(), "Flow type ID");

    // Validate JSON schemas
    validateJsonSchema(dto.getCredentialSchema(), "Credential schema");
    if (dto.getInputSchema() != null) {
      validateJsonSchema(dto.getInputSchema(), "Input schema");
    }

    // Set default inputSchema if null
    if (dto.getInputSchema() == null) {
      dto.setInputSchema("{}");
    }
  }

  private void validateUpdateRequest(FlowTargetDto dto) {
    validateNotNull(dto, "Flow target DTO");
    validateNotBlank(dto.getName(), "Flow target name");
    validateNotBlank(dto.getLogo(), "Flow target logo");
    validateNotNull(dto.getStatus(), "Flow target status");
    validateNotBlank(dto.getCredentialSchema(), "Credential schema");

    // Validate JSON schemas
    validateJsonSchema(dto.getCredentialSchema(), "Credential schema");
    if (dto.getInputSchema() != null) {
      validateJsonSchema(dto.getInputSchema(), "Input schema");
    }

    // Set default inputSchema if null
    if (dto.getInputSchema() == null) {
      dto.setInputSchema("{}");
    }
  }

  private void validateJsonSchema(String jsonString, String fieldName) {
    try {
      // Basic JSON validation
      if (jsonString == null || jsonString.trim().isEmpty()) {
        throw new ValidationException(fieldName + " cannot be empty");
      }

      // Try to parse JSON
      Json.of(jsonString);
    } catch (Exception e) {
      throw new ValidationException("Invalid " + fieldName + " JSON format: " + e.getMessage());
    }
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createFlowTarget(
      String flowTypeId, FlowTargetDto dto) {
    FlowTarget flowTarget =
        FlowTarget.create(
            dto.getName(),
            dto.getLogo(),
            dto.getStatus(),
            dto.getCredentialSchema(),
            dto.getInputSchema(),
            dto.getCurrencies(),
            flowTypeId,
            dto.getBrandId());

    return flowTargetRepository
        .insertFlowTarget(
            flowTarget.getId(),
            flowTarget.getName(),
            flowTarget.getLogo(),
            flowTarget.getStatus().getValue(),
            flowTarget.getCredentialSchema(),
            flowTarget.getInputSchema(),
            flowTarget.getCurrencies() != null
                ? flowTarget.getCurrencies().toArray(new String[0])
                : new String[0],
            flowTarget.getFlowTypeId(),
            flowTarget.getBrandId(),
            flowTarget.getCreatedAt(),
            flowTarget.getUpdatedAt(),
            flowTarget.getCreatedBy(),
            flowTarget.getUpdatedBy())
        .then(
            Mono.defer(
                () -> {
                  FlowTargetDto responseDto = FlowTargetDto.fromEntity(flowTarget);
                  return successResponse(responseDto, "Flow target created successfully");
                }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateFlowTarget(
      String flowTypeId, String id, FlowTargetDto dto) {
    return flowTargetRepository
        .findById(id)
        .flatMap(
            flowTarget -> {
              flowTarget.updateDetails(
                  dto.getName(),
                  dto.getLogo(),
                  dto.getStatus(),
                  dto.getCredentialSchema(),
                  dto.getInputSchema(),
                  dto.getCurrencies(),
                  dto.getBrandId());
              return flowTargetRepository.updateFlowTarget(
                  id,
                  dto.getName(),
                  dto.getLogo(),
                  dto.getStatus().getValue(),
                  flowTarget.getCredentialSchema(),
                  flowTarget.getInputSchema(),
                  dto.getCurrencies() != null
                      ? dto.getCurrencies().toArray(new String[0])
                      : flowTarget.getCurrencies() != null
                          ? flowTarget.getCurrencies().toArray(new String[0])
                          : new String[0],
                  flowTarget.getBrandId(),
                  flowTarget.getUpdatedAt());
            })
        .then(
            flowTargetRepository
                .findById(id)
                .flatMap(
                    savedFlowTarget ->
                        Mono.defer(
                            () -> {
                              FlowTargetDto responseDto = FlowTargetDto.fromEntity(savedFlowTarget);
                              return successResponse(
                                  responseDto, "Flow target updated successfully");
                            })));
  }

  @Override
  public Mono<FlowTarget> getFlowTargetById(String id) {
    validateNotBlank(id, "Flow target ID");
    return flowTargetRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Flow target", "id", id)));
  }

  private Mono<FlowTargetDto> buildFlowTargetWithAssociations(FlowTarget flowTarget) {
    return flowDefinitionRepository
        .findByFlowTargetId(flowTarget.getId())
        .collectList()
        .flatMap(
            flowDefinitions -> {
              List<Mono<FlowTargetDto.SupportedActionInfo>> flowDefinitionMonos =
                  flowDefinitions.stream()
                      .map(
                          fd ->
                              flowActionRepository
                                  .findById(fd.getFlowActionId())
                                  .map(
                                      flowAction ->
                                          FlowTargetDto.SupportedActionInfo.builder()
                                              .id(fd.getId())
                                              .flowActionId(fd.getFlowActionId())
                                              .flowActionName(flowAction.getName())
                                              .build())
                                  .onErrorReturn(
                                      FlowTargetDto.SupportedActionInfo.builder()
                                          .id(fd.getId())
                                          .flowActionId(fd.getFlowActionId())
                                          .flowActionName("Unknown")
                                          .build()))
                      .collect(Collectors.toList());

              return Flux.fromIterable(flowDefinitionMonos)
                  .flatMap(mono -> mono)
                  .collectList()
                  .map(
                      supportedActions -> {
                        FlowTargetDto dto = FlowTargetDto.fromEntity(flowTarget);
                        dto.setSupportedActions(supportedActions);
                        return dto;
                      });
            });
  }
}
