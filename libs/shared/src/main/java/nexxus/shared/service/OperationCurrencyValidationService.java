package nexxus.shared.service;

import nexxus.shared.dto.OperationCurrencyValidationRequest;
import nexxus.shared.dto.ValidationResult;

import reactor.core.publisher.Mono;

/** Service interface for validating operation currencies against flow targets. */
public interface OperationCurrencyValidationService {

  /**
   * Validates operation currencies against the flow target's supported currencies. Returns
   * ValidationResult.success() if all currencies are supported, otherwise returns
   * ValidationResult.failure() with appropriate error message.
   *
   * @param request The request containing flow target ID and operations to validate
   * @return Mono containing ValidationResult
   */
  Mono<ValidationResult> validateOperationCurrenciesAgainstFlowTarget(
      OperationCurrencyValidationRequest request);
}
