package nexxus.flowdefinition.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowdefinition.dto.FlowDefinitionDto;
import nexxus.flowdefinition.entity.FlowDefinition;
import nexxus.flowdefinition.repository.FlowDefinitionRepository;
import nexxus.flowdefinition.service.FlowDefinitionService;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlowDefinitionServiceImpl implements FlowDefinitionService {

  private final FlowDefinitionRepository flowDefinitionRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(FlowDefinitionDto dto) {
    try {
      validateCreateRequest(dto);

      return flowDefinitionRepository
          .findByCodeAndBrandId(dto.getCode(), dto.getBrandId())
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return conflictError(
                      "Flow definition already exists with code: " + dto.getCode());
                } else {
                  return createFlowDefinition(dto);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "creating flow definition");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return flowDefinitionRepository
        .findAll()
        .collectList()
        .map(
            flowDefinitions -> flowDefinitions.stream().map(FlowDefinitionDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving flow definitions"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAllByFlowTargetId(String flowTargetId) {
    validateNotBlank(flowTargetId, "Flow target ID");

    return flowDefinitionRepository
        .findByFlowTargetId(flowTargetId)
        .collectList()
        .map(
            flowDefinitions -> flowDefinitions.stream().map(FlowDefinitionDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving flow definitions by flow target"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> read(String id) {
    validateNotBlank(id, "Flow definition ID");

    return flowDefinitionRepository
        .findById(id)
        .flatMap(
            flowDefinition ->
                Mono.just(FlowDefinitionDto.fromEntity(flowDefinition))
                    .flatMap(this::successResponse))
        .switchIfEmpty(notFoundError("Flow definition", "id", id))
        .onErrorResume(e -> databaseError(e, "retrieving flow definition"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FlowDefinitionDto dto) {
    try {
      validateNotBlank(id, "Flow definition ID");
      validateUpdateRequest(dto);

      return flowDefinitionRepository
          .findById(id)
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return updateFlowDefinition(id, dto);
                } else {
                  return notFoundError("Flow definition", "id", id);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "updating flow definition");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    validateNotBlank(id, "Flow definition ID");

    return flowDefinitionRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return flowDefinitionRepository
                    .deleteById(id)
                    .then(successResponse("Flow definition deleted successfully"));
              } else {
                return notFoundError("Flow definition", "id", id);
              }
            })
        .onErrorResume(
            e -> {
              String errorMessage = e.getMessage();
              if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                return conflictError(
                    "Cannot delete flow definition: It has associated data. Please remove the associated data first.");
              }
              return databaseError("Error deleting flow definition: " + errorMessage);
            });
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAllByBrand() {
    // In a real implementation, this would come from the security context or request headers
    String brandId =
        getCurrentBrandId(); // This method needs to be implemented based on your context management

    return flowDefinitionRepository
        .findByBrandId(brandId)
        .collectList()
        .map(
            flowDefinitions -> flowDefinitions.stream().map(FlowDefinitionDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving flow definitions by brand"));
  }

  // This should be implemented based on your application's context management
  private String getCurrentBrandId() {
    // For now, return null to get all flow definitions (both brand-specific and global)
    // In a real implementation, this would come from:
    // - Security context
    // - Request headers
    // - JWT token
    // - Session context
    return null;
  }

  private void validateCreateRequest(FlowDefinitionDto dto) {
    validateNotNull(dto, "Flow definition DTO");
    validateNotBlank(dto.getFlowActionId(), "Flow action ID");
    validateNotBlank(dto.getFlowTargetId(), "Flow target ID");
    validateNotBlank(dto.getCode(), "Code");
  }

  private void validateUpdateRequest(FlowDefinitionDto dto) {
    validateNotNull(dto, "Flow definition DTO");
    validateNotBlank(dto.getFlowActionId(), "Flow action ID");
    validateNotBlank(dto.getFlowTargetId(), "Flow target ID");
    validateNotBlank(dto.getCode(), "Code");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createFlowDefinition(FlowDefinitionDto dto) {
    FlowDefinition flowDefinition =
        FlowDefinition.create(
            dto.getFlowActionId(),
            dto.getFlowTargetId(),
            dto.getDescription(),
            dto.getCode(),
            dto.getBrandId());

    return flowDefinitionRepository
        .insertFlowDefinition(
            flowDefinition.getId(),
            flowDefinition.getFlowActionId(),
            flowDefinition.getFlowTargetId(),
            flowDefinition.getDescription(),
            flowDefinition.getCode(),
            flowDefinition.getBrandId(),
            flowDefinition.getCreatedAt(),
            flowDefinition.getUpdatedAt(),
            flowDefinition.getCreatedBy(),
            flowDefinition.getUpdatedBy())
        .then(
            Mono.defer(
                () -> {
                  FlowDefinitionDto responseDto = FlowDefinitionDto.fromEntity(flowDefinition);
                  return successResponse(responseDto, "Flow definition created successfully");
                }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateFlowDefinition(
      String id, FlowDefinitionDto dto) {
    return flowDefinitionRepository
        .findById(id)
        .flatMap(
            flowDefinition -> {
              flowDefinition.updateDetails(
                  dto.getFlowActionId(),
                  dto.getFlowTargetId(),
                  dto.getDescription(),
                  dto.getCode(),
                  dto.getBrandId());
              return flowDefinitionRepository.updateFlowDefinition(
                  id,
                  dto.getFlowActionId(),
                  dto.getFlowTargetId(),
                  dto.getDescription(),
                  dto.getCode(),
                  flowDefinition.getBrandId(),
                  flowDefinition.getUpdatedAt());
            })
        .then(
            flowDefinitionRepository
                .findById(id)
                .flatMap(
                    savedFlowDefinition ->
                        Mono.defer(
                            () -> {
                              FlowDefinitionDto responseDto =
                                  FlowDefinitionDto.fromEntity(savedFlowDefinition);
                              return successResponse(
                                  responseDto, "Flow definition updated successfully");
                            })));
  }
}
