package nexxus.flowaction.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowaction.dto.FlowActionDto;
import nexxus.flowaction.entity.FlowAction;
import nexxus.flowaction.repository.FlowActionRepository;
import nexxus.flowaction.service.FlowActionService;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.util.ReactiveResponseHandler;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlowActionServiceImpl implements FlowActionService {

  private final FlowActionRepository flowActionRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(FlowActionDto dto) {
    try {
      validateCreateRequest(dto);

      return flowActionRepository
          .findByNameAndFlowTypeId(dto.getName(), dto.getFlowTypeId())
          .flatMap(
              existing -> conflictError("Flow action already exists with name: " + dto.getName()))
          .switchIfEmpty(createFlowAction(dto));

    } catch (Exception e) {
      return databaseError(e, "creating flow action");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return flowActionRepository
        .findAll()
        .collectList()
        .map(flowActions -> flowActions.stream().map(FlowActionDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving flow actions"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> read(String id) {
    validateNotBlank(id, "Flow action ID");

    return flowActionRepository
        .findById(id)
        .flatMap(
            flowAction ->
                Mono.just(FlowActionDto.fromEntity(flowAction)).flatMap(this::successResponse))
        .switchIfEmpty(notFoundError("Flow action", "id", id))
        .onErrorResume(e -> databaseError(e, "retrieving flow action"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FlowActionDto dto) {
    try {
      validateNotBlank(id, "Flow action ID");
      validateUpdateRequest(dto);

      return flowActionRepository
          .findById(id)
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return updateFlowAction(id, dto);
                } else {
                  return notFoundError("Flow action", "id", id);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "updating flow action");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    validateNotBlank(id, "Flow action ID");

    return flowActionRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return flowActionRepository
                    .deleteById(id)
                    .then(successResponse("Flow action deleted successfully"));
              } else {
                return notFoundError("Flow action", "id", id);
              }
            })
        .onErrorResume(e -> databaseError(e, "deleting flow action"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findByFlowTypeId(String flowTypeId) {
    validateNotBlank(flowTypeId, "Flow type ID");

    return flowActionRepository
        .findByFlowTypeId(flowTypeId)
        .collectList()
        .map(flowActions -> flowActions.stream().map(FlowActionDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving flow actions for flow type"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findByNameAndFlowTypeId(
      String name, String flowTypeId) {
    validateNotBlank(name, "Flow action name");
    validateNotBlank(flowTypeId, "Flow type ID");

    return flowActionRepository
        .findByNameAndFlowTypeId(name, flowTypeId)
        .map(FlowActionDto::fromEntity)
        .flatMap(this::successResponse)
        .switchIfEmpty(
            notFoundError("Flow action", "name and flow type id", name + ":" + flowTypeId))
        .onErrorResume(e -> databaseError(e, "retrieving flow action by name and flow type id"));
  }

  private void validateCreateRequest(FlowActionDto dto) {
    validateNotNull(dto, "Flow action DTO");
    validateNotBlank(dto.getName(), "Flow action name");
    validateNotBlank(dto.getFlowTypeId(), "Flow type ID");
    validateNotNull(dto.getSteps(), "Flow action steps");
    validateCondition(!dto.getSteps().isEmpty(), "Flow action steps cannot be empty");
    validateNotBlank(dto.getInputSchema(), "Input schema");
    validateNotBlank(dto.getOutputSchema(), "Output schema");

    // Validate JSON schemas
    validateJsonSchema(dto.getInputSchema(), "Input schema");
    validateJsonSchema(dto.getOutputSchema(), "Output schema");
  }

  private void validateUpdateRequest(FlowActionDto dto) {
    validateNotNull(dto, "Flow action DTO");
    validateNotBlank(dto.getName(), "Flow action name");
    validateNotNull(dto.getSteps(), "Flow action steps");
    validateCondition(!dto.getSteps().isEmpty(), "Flow action steps cannot be empty");
    validateNotBlank(dto.getInputSchema(), "Input schema");
    validateNotBlank(dto.getOutputSchema(), "Output schema");

    // Validate JSON schemas
    validateJsonSchema(dto.getInputSchema(), "Input schema");
    validateJsonSchema(dto.getOutputSchema(), "Output schema");
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

  private Mono<ResponseEntity<ApiResponse<Object>>> createFlowAction(FlowActionDto dto) {
    FlowAction flowAction =
        FlowAction.create(
            dto.getName(),
            dto.getSteps(),
            dto.getFlowTypeId(),
            dto.getInputSchema(),
            dto.getOutputSchema());

    return flowActionRepository
        .insertFlowAction(
            flowAction.getId(),
            flowAction.getName(),
            flowAction.getSteps().toArray(new String[0]),
            flowAction.getFlowTypeId(),
            flowAction.getInputSchema(),
            flowAction.getOutputSchema(),
            flowAction.getCreatedAt(),
            flowAction.getUpdatedAt(),
            flowAction.getCreatedBy(),
            flowAction.getUpdatedBy())
        .then(
            Mono.defer(
                () -> {
                  FlowActionDto responseDto = FlowActionDto.fromEntity(flowAction);
                  return successResponse(responseDto, "Flow action created successfully");
                }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateFlowAction(String id, FlowActionDto dto) {
    return flowActionRepository
        .updateFlowAction(
            id,
            dto.getName(),
            dto.getSteps().toArray(new String[0]),
            Json.of(dto.getInputSchema()),
            Json.of(dto.getOutputSchema()),
            LocalDateTime.now())
        .then(
            flowActionRepository
                .findById(id)
                .flatMap(
                    savedFlowAction ->
                        Mono.defer(
                            () -> {
                              FlowActionDto responseDto = FlowActionDto.fromEntity(savedFlowAction);
                              return successResponse(
                                  responseDto, "Flow action updated successfully");
                            })));
  }
}
