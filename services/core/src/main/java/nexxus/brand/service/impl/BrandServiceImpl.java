package nexxus.brand.service.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.brand.dto.BrandDto;
import nexxus.brand.entity.Brand;
import nexxus.brand.repository.BrandRepository;
import nexxus.brand.service.BrandService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

  private final BrandRepository brandRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(BrandDto brandDto) {
    try {
      validateCreateRequest(brandDto);

      return brandRepository
          .findByName(brandDto.getName())
          .flatMap(
              existing ->
                  customError(
                      ErrorCode.BRAND_ALREADY_EXISTS,
                      "Brand already exists with name: " + brandDto.getName(),
                      org.springframework.http.HttpStatus.CONFLICT))
          .switchIfEmpty(createBrand(brandDto));

    } catch (Exception e) {
      return databaseError(e, "creating brand");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> readAll() {
    return brandRepository
        .findAll()
        .collectList()
        .flatMap(
            brands -> {
              List<BrandDto> brandDtos = brands.stream().map(BrandDto::fromEntity).toList();
              return successResponse(brandDtos, "Brands retrieved successfully");
            })
        .onErrorResume(e -> databaseError(e, "retrieving brands"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> read(String id) {
    validateNotBlank(id, "Brand ID");

    return brandRepository
        .findById(id)
        .flatMap(brand -> Mono.just(BrandDto.fromEntity(brand)).flatMap(this::successResponse))
        .switchIfEmpty(
            customError(
                ErrorCode.BRAND_NOT_FOUND,
                "Brand not found with ID: " + id,
                org.springframework.http.HttpStatus.NOT_FOUND))
        .onErrorResume(e -> databaseError(e, "retrieving brand"));
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> update(String id, BrandDto brandDto) {
    try {
      validateNotBlank(id, "Brand ID");
      validateUpdateRequest(brandDto);

      return brandRepository
          .findById(id)
          .hasElement()
          .flatMap(
              exists -> {
                if (exists) {
                  return updateBrand(id, brandDto);
                } else {
                  return customError(
                      ErrorCode.BRAND_NOT_FOUND,
                      "Brand not found with ID: " + id,
                      org.springframework.http.HttpStatus.NOT_FOUND);
                }
              });

    } catch (Exception e) {
      return databaseError(e, "updating brand");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> delete(String id) {
    validateNotBlank(id, "Brand ID");

    return brandRepository
        .findById(id)
        .hasElement()
        .flatMap(
            exists -> {
              if (exists) {
                return brandRepository
                    .deleteById(id)
                    .then(successResponse("Brand deleted successfully"));
              } else {
                return customError(
                    ErrorCode.BRAND_NOT_FOUND,
                    "Brand not found with ID: " + id,
                    org.springframework.http.HttpStatus.NOT_FOUND);
              }
            })
        .onErrorResume(e -> databaseError(e, "deleting brand"));
  }

  private void validateCreateRequest(BrandDto brandDto) {
    validateNotNull(brandDto, "Brand DTO");
    validateNotBlank(brandDto.getName(), "Brand name");
  }

  private void validateUpdateRequest(BrandDto brandDto) {
    validateNotNull(brandDto, "Brand DTO");
    validateNotBlank(brandDto.getName(), "Brand name");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createBrand(BrandDto brandDto) {
    Brand brand = Brand.create(brandDto.getName());

    return brandRepository
        .insertBrand(brand.getId(), brand.getName(), brand.getCreatedAt(), brand.getUpdatedAt())
        .then(
            Mono.defer(
                () -> {
                  BrandDto responseDto = BrandDto.fromEntity(brand);
                  return successResponse(responseDto, "Brand created successfully");
                }));
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> updateBrand(String id, BrandDto brandDto) {
    return brandRepository
        .findById(id)
        .flatMap(
            brand -> {
              brand.updateDetails(brandDto.getName());
              return brandRepository.save(brand);
            })
        .flatMap(
            savedBrand ->
                Mono.defer(
                    () -> {
                      BrandDto responseDto = BrandDto.fromEntity(savedBrand);
                      return successResponse(responseDto, "Brand updated successfully");
                    }));
  }
}
