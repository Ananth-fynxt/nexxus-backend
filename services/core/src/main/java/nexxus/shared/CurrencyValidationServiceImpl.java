package nexxus.shared.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.psp.repository.CurrencyLimitRepository;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.dto.ValidationResult;
import nexxus.shared.service.CurrencyValidationService;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of currency validation service. Uses the currency_limits table to validate PSP
 * currency support.
 */
@Service
@RequiredArgsConstructor
public class CurrencyValidationServiceImpl implements CurrencyValidationService {

  private final CurrencyLimitRepository currencyLimitRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public Mono<Boolean> validatePspCurrencySupportBoolean(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    if (pspIds == null || pspIds.isEmpty()) {
      return Mono.just(true); // No PSPs to validate
    }

    // Check if all PSPs have currency_limits entries for the given currency
    return Flux.fromIterable(pspIds)
        .flatMap(
            pspId ->
                currencyLimitRepository.existsByCompositeKeyAndCurrency(
                    brandId, environmentId, flowActionId, pspId, currency))
        .all(supportsCurrency -> supportsCurrency); // All PSPs must support the currency
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    return validatePspCurrencySupportBoolean(currency, pspIds, brandId, environmentId, flowActionId)
        .flatMap(
            allSupported -> {
              if (!allSupported) {
                return getUnsupportedPsps(currency, pspIds, brandId, environmentId, flowActionId)
                    .flatMap(
                        unsupportedPsps -> {
                          String errorMessage =
                              "Currency "
                                  + currency
                                  + " is not supported by PSPs: "
                                  + String.join(", ", unsupportedPsps);
                          return Mono.just(responseHandler.validationError(errorMessage));
                        });
              }
              return Mono.empty();
            });
  }

  @Override
  public Mono<ValidationResult> validateCurrencySupportWithFlowTargetFallback(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    // First check PSP currency support using currency_limits table
    return validatePspCurrencySupportBoolean(currency, pspIds, brandId, environmentId, flowActionId)
        .flatMap(
            allPspSupported -> {
              if (allPspSupported) {
                // All PSPs support the currency, validation passes
                return Mono.just(ValidationResult.success());
              } else {
                // PSP validation failed, check if any PSPs support this currency via their flow
                // targets
                return currencyLimitRepository
                    .findSupportedPspIdsByCurrency(brandId, environmentId, flowActionId, currency)
                    .collectList()
                    .map(
                        supportedPspIds -> {
                          // Check if any of the requested PSPs are in the supported list
                          boolean anyPspSupports =
                              pspIds.stream().anyMatch(supportedPspIds::contains);

                          if (!anyPspSupports) {
                            // None of the requested PSPs support this currency
                            String errorMessage =
                                "Currency "
                                    + currency
                                    + " is not supported by any of the specified PSPs";
                            ResponseEntity<ApiResponse<Object>> errorResponse =
                                responseHandler.validationError(errorMessage);
                            return ValidationResult.failure(errorResponse);
                          }
                          return ValidationResult
                              .success(); // At least one PSP supports via flow target
                        });
              }
            });
  }

  @Override
  public Mono<List<String>> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    if (pspIds == null || pspIds.isEmpty()) {
      return Mono.just(new ArrayList<>());
    }

    // Check each PSP to see if it has a currency_limits entry for the currency
    return Flux.fromIterable(pspIds)
        .flatMap(
            pspId ->
                currencyLimitRepository
                    .existsByCompositeKeyAndCurrency(
                        brandId, environmentId, flowActionId, pspId, currency)
                    .map(
                        supports -> supports ? null : pspId)) // Return PSP ID if it doesn't support
        .filter(pspId -> pspId != null) // Filter out null values (PSPs that support)
        .collectList();
  }

  @Override
  public Mono<Boolean> validateFlowTargetCurrencySupport(
      String currency, String brandId, String environmentId, String flowActionId) {

    // Check if any PSPs support this currency for the given context
    // This indicates that flow targets support the currency through their PSPs
    return currencyLimitRepository
        .findSupportedPspIdsByCurrency(brandId, environmentId, flowActionId, currency)
        .hasElements(); // Return true if any PSPs support this currency
  }
}
