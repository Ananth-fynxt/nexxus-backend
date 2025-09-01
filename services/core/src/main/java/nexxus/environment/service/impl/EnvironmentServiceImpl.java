package nexxus.environment.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.environment.dto.EnvironmentDto;
import nexxus.environment.entity.Environment;
import nexxus.environment.repository.EnvironmentRepository;
import nexxus.environment.service.EnvironmentService;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

  private final EnvironmentRepository environmentRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(EnvironmentDto dto) {
    try {
      validateCreateRequest(dto);

      return environmentRepository
          .findByNameAndBrandId(dto.getName(), dto.getBrandId())
          .flatMap(
              existing ->
                  conflictError(
                      "Environment already exists with name: "
                          + dto.getName()
                          + " for brand: "
                          + dto.getBrandId()))
          .switchIfEmpty(createEnvironment(dto));

    } catch (Exception e) {
      return databaseError(e, "creating environment");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return environmentRepository
        .findAll()
        .collectList()
        .map(environments -> environments.stream().map(EnvironmentDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving environments"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> read(String id) {
    validateNotBlank(id, "Environment ID");

    return environmentRepository
        .findById(id)
        .flatMap(
            environment ->
                Mono.just(EnvironmentDto.fromEntity(environment)).flatMap(this::successResponse))
        .switchIfEmpty(notFoundError("Environment", "id", id))
        .onErrorResume(e -> databaseError(e, "retrieving environment"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, EnvironmentDto dto) {
    try {
      validateNotBlank(id, "Environment ID");
      validateUpdateRequest(dto);

      return environmentRepository
          .findById(id)
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return updateEnvironment(id, dto);
                } else {
                  return notFoundError("Environment", "id", id);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "updating environment");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    validateNotBlank(id, "Environment ID");

    return environmentRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return environmentRepository
                    .deleteById(id)
                    .then(successResponse("Environment deleted successfully"));
              } else {
                return notFoundError("Environment", "id", id);
              }
            })
        .onErrorResume(e -> databaseError(e, "deleting environment"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> findByBrandId(String brandId) {
    validateNotBlank(brandId, "Brand ID");

    return environmentRepository
        .findByBrandId(brandId)
        .collectList()
        .map(environments -> environments.stream().map(EnvironmentDto::fromEntity).toList())
        .flatMap(this::successResponse)
        .onErrorResume(e -> databaseError(e, "retrieving environments for brand"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> rotateSecret(String id) {
    validateNotBlank(id, "Environment ID");

    return environmentRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return rotateEnvironmentSecret(id);
              } else {
                return notFoundError("Environment", "id", id);
              }
            })
        .onErrorResume(e -> databaseError(e, "rotating environment secret"));
  }

  private void validateCreateRequest(EnvironmentDto dto) {
    validateNotNull(dto, "Environment DTO");
    validateNotBlank(dto.getName(), "Environment name");
    validateNotBlank(dto.getBrandId(), "Brand ID");
    validateNotBlank(dto.getOrigin(), "Origin URL");
  }

  private void validateUpdateRequest(EnvironmentDto dto) {
    validateNotNull(dto, "Environment DTO");
    validateNotBlank(dto.getName(), "Environment name");
    validateNotBlank(dto.getBrandId(), "Brand ID");
    validateNotBlank(dto.getOrigin(), "Origin URL");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createEnvironment(EnvironmentDto dto) {
    Environment environment = Environment.create(dto.getName(), dto.getBrandId(), dto.getOrigin());

    return environmentRepository
        .insertEnvironment(
            environment.getId(),
            environment.getName(),
            environment.getBrandId(),
            environment.getOrigin(),
            environment.getSecret(),
            environment.getToken(),
            environment.getCreatedAt(),
            environment.getUpdatedAt(),
            environment.getCreatedBy(),
            environment.getUpdatedBy())
        .then(
            Mono.defer(
                () -> {
                  EnvironmentDto responseDto = EnvironmentDto.fromEntity(environment);
                  return successResponse(responseDto, "Environment created successfully");
                }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateEnvironment(
      String id, EnvironmentDto dto) {
    return environmentRepository
        .findById(id)
        .flatMap(
            environment -> {
              environment.updateDetails(dto.getName(), dto.getBrandId(), dto.getOrigin());
              return environmentRepository.save(environment);
            })
        .flatMap(
            savedEnvironment ->
                Mono.defer(
                    () -> {
                      EnvironmentDto responseDto = EnvironmentDto.fromEntity(savedEnvironment);
                      return successResponse(responseDto, "Environment updated successfully");
                    }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> rotateEnvironmentSecret(String id) {
    return environmentRepository
        .findById(id)
        .flatMap(
            environment -> {
              environment.rotateSecret();
              return environmentRepository.save(environment);
            })
        .flatMap(
            savedEnvironment ->
                Mono.defer(
                    () -> {
                      EnvironmentDto responseDto = EnvironmentDto.fromEntity(savedEnvironment);
                      return successResponse(
                          responseDto, "Environment secret rotated successfully");
                    }));
  }
}
