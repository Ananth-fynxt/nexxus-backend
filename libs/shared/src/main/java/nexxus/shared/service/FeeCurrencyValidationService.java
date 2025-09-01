package nexxus.shared.service;

import java.util.List;

import reactor.core.publisher.Mono;

/** Service interface for validating PSP currency support for fee operations. */
public interface FeeCurrencyValidationService {

  /**
   * Validates that all provided PSPs support the given currency. First checks supported_currencies
   * table, then falls back to flow_targets table.
   *
   * @param currency the currency to validate
   * @param pspIds list of PSP IDs to validate
   * @param brandId the brand ID
   * @param environmentId the environment ID
   * @param flowActionId the flow action ID
   * @return Mono containing true if all PSPs support the currency, false otherwise
   */
  Mono<Boolean> validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  /**
   * Gets list of unsupported PSPs for a given currency.
   *
   * @param currency the currency to check
   * @param pspIds list of PSP IDs to check
   * @param brandId the brand ID
   * @param environmentId the environment ID
   * @param flowActionId the flow action ID
   * @return Mono containing list of PSP IDs that do not support the currency
   */
  Mono<List<String>> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);
}
