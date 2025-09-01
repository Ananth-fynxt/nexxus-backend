package nexxus.shared.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import nexxus.shared.dto.ApiResponse;

import reactor.core.publisher.Mono;

/**
 * Common service for validating currency support across different entities. Provides centralized
 * currency validation logic for PSP and Flow Target support.
 */
public interface CurrencyValidationService {

  /**
   * Validates if all PSPs in the list support the given currency. Returns true if all PSPs support
   * the currency, false otherwise.
   *
   * @param currency The currency to validate
   * @param pspIds List of PSP IDs to check
   * @param brandId Brand ID for context
   * @param environmentId Environment ID for context
   * @param flowActionId Flow Action ID for context
   * @return Mono<Boolean> true if all PSPs support the currency, false otherwise
   */
  Mono<Boolean> validatePspCurrencySupportBoolean(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  /**
   * Validates if all PSPs in the list support the given currency. If validation fails, returns a
   * Mono with validation error. If validation passes, returns Mono.empty().
   *
   * @param currency The currency to validate
   * @param pspIds List of PSP IDs to check
   * @param brandId Brand ID for context
   * @param environmentId Environment ID for context
   * @param flowActionId Flow Action ID for context
   * @return Mono<ResponseEntity<ApiResponse<Object>>> with error if validation fails, Mono.empty()
   *     if successful
   */
  Mono<ResponseEntity<ApiResponse<Object>>> validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  /**
   * Validates currency support with fallback to flow target. First checks PSP currency support, if
   * that fails, checks flow target currency support. Only fails if both PSP and flow target don't
   * support the currency.
   *
   * @param currency The currency to validate
   * @param pspIds List of PSP IDs to check
   * @param brandId Brand ID for context
   * @param environmentId Environment ID for context
   * @param flowActionId Flow Action ID for context
   * @return Mono<ValidationResult> with validation outcome
   */
  Mono<nexxus.shared.dto.ValidationResult> validateCurrencySupportWithFlowTargetFallback(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  /**
   * Gets list of PSP IDs that don't support the given currency.
   *
   * @param currency The currency to check
   * @param pspIds List of PSP IDs to validate
   * @param brandId Brand ID for context
   * @param environmentId Environment ID for context
   * @param flowActionId Flow Action ID for context
   * @return Mono<List<String>> list of PSP IDs that don't support the currency
   */
  Mono<List<String>> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  /**
   * Checks if the flow target supports the given currency.
   *
   * @param currency The currency to check
   * @param brandId Brand ID for context
   * @param environmentId Environment ID for context
   * @param flowActionId Flow Action ID for context
   * @return Mono<Boolean> true if flow target supports the currency, false otherwise
   */
  Mono<Boolean> validateFlowTargetCurrencySupport(
      String currency, String brandId, String environmentId, String flowActionId);
}
