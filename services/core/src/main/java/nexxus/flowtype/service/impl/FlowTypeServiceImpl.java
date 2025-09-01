package nexxus.flowtype.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowtype.dto.FlowTypeDto;
import nexxus.flowtype.entity.FlowType;
import nexxus.flowtype.repository.FlowTypeRepository;
import nexxus.flowtype.service.FlowTypeService;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlowTypeServiceImpl implements FlowTypeService {

  private final FlowTypeRepository flowTypeRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(FlowTypeDto dto) {
    try {
      validateCreateRequest(dto);

      return flowTypeRepository
          .findByName(dto.getName())
          .flatMap(
              existing -> conflictError("Flow type already exists with name: " + dto.getName()))
          .switchIfEmpty(createFlowType(dto));

    } catch (Exception e) {
      return databaseError(e, "creating flow type");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return flowTypeRepository
        .findAll()
        .collectList()
        .map(flowTypes -> flowTypes.stream().map(FlowTypeDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving flow types"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> read(String id) {
    validateNotBlank(id, "Flow type ID");

    return flowTypeRepository
        .findById(id)
        .flatMap(
            flowType -> Mono.just(FlowTypeDto.fromEntity(flowType)).flatMap(this::successResponse))
        .switchIfEmpty(notFoundError("Flow type", "id", id))
        .onErrorResume(e -> databaseError(e, "retrieving flow type"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, FlowTypeDto dto) {
    try {
      validateNotBlank(id, "Flow type ID");
      validateUpdateRequest(dto);

      return flowTypeRepository
          .findById(id)
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return updateFlowType(id, dto);
                } else {
                  return notFoundError("Flow type", "id", id);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "updating flow type");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    validateNotBlank(id, "Flow type ID");

    return flowTypeRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return flowTypeRepository
                    .deleteById(id)
                    .then(successResponse("Flow type deleted successfully"));
              } else {
                return notFoundError("Flow type", "id", id);
              }
            })
        .onErrorResume(
            e -> {
              String errorMessage = e.getMessage();
              if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                return conflictError(
                    "Cannot delete flow type: It has associated flow actions. Please delete the flow actions first.");
              }
              return databaseError("Error deleting flow type: " + errorMessage);
            });
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findByName(String name) {
    validateNotBlank(name, "Flow type name");

    return flowTypeRepository
        .findByName(name)
        .flatMap(
            flowType -> Mono.just(FlowTypeDto.fromEntity(flowType)).flatMap(this::successResponse))
        .switchIfEmpty(notFoundError("Flow type", "name", name))
        .onErrorResume(e -> databaseError(e, "retrieving flow type by name"));
  }

  private void validateCreateRequest(FlowTypeDto dto) {
    validateNotNull(dto, "Flow type DTO");
    validateNotBlank(dto.getName(), "Flow type name");
  }

  private void validateUpdateRequest(FlowTypeDto dto) {
    validateNotNull(dto, "Flow type DTO");
    validateNotBlank(dto.getName(), "Flow type name");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createFlowType(FlowTypeDto dto) {
    FlowType flowType = FlowType.create(dto.getName());

    return flowTypeRepository
        .insertFlowType(
            flowType.getId(),
            flowType.getName(),
            flowType.getCreatedAt(),
            flowType.getUpdatedAt(),
            flowType.getCreatedBy(),
            flowType.getUpdatedBy())
        .then(
            Mono.defer(
                () -> {
                  FlowTypeDto responseDto = FlowTypeDto.fromEntity(flowType);
                  return successResponse(responseDto, "Flow type created successfully");
                }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateFlowType(String id, FlowTypeDto dto) {
    return flowTypeRepository
        .findById(id)
        .flatMap(
            flowType -> {
              flowType.updateDetails(dto.getName());
              return flowTypeRepository.updateFlowType(id, dto.getName(), flowType.getUpdatedAt());
            })
        .then(
            Mono.defer(
                () -> {
                  FlowTypeDto responseDto =
                      FlowTypeDto.fromEntity(
                          FlowType.builder()
                              .id(id)
                              .name(dto.getName())
                              .createdAt(LocalDateTime.now())
                              .updatedAt(LocalDateTime.now())
                              .createdBy("system")
                              .updatedBy("system")
                              .build());
                  return successResponse(responseDto, "Flow type updated successfully");
                }));
  }
}
