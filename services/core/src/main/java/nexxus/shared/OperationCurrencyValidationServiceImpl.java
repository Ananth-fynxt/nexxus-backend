package nexxus.shared;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nexxus.flowtarget.repository.FlowTargetRepository;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.dto.OperationCurrencyValidationRequest;
import nexxus.shared.dto.ValidationResult;
import nexxus.shared.service.OperationCurrencyValidationService;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Implementation of operation currency validation service. Validates operation currencies against
 * flow target supported currencies.
 */
@Service
@RequiredArgsConstructor
public class OperationCurrencyValidationServiceImpl implements OperationCurrencyValidationService {

  private final FlowTargetRepository flowTargetRepository;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public Mono<ValidationResult> validateOperationCurrenciesAgainstFlowTarget(
      OperationCurrencyValidationRequest request) {
    if (request.getOperations() == null || request.getOperations().isEmpty()) {
      return Mono.just(ValidationResult.success());
    }

    return flowTargetRepository
        .findById(request.getFlowTargetId())
        .map(
            flowTarget -> {
              if (flowTarget == null) {
                ResponseEntity<ApiResponse<Object>> errorResponse =
                    responseHandler.errorResponse(
                        ErrorCode.PSP_CONFIGURATION_ERROR,
                        "Flow target not found: " + request.getFlowTargetId(),
                        HttpStatus.BAD_REQUEST);
                return ValidationResult.failure(errorResponse);
              }

              List<String> supportedCurrencies = flowTarget.getCurrencies();
              if (supportedCurrencies == null || supportedCurrencies.isEmpty()) {
                ResponseEntity<ApiResponse<Object>> errorResponse =
                    responseHandler.errorResponse(
                        ErrorCode.PSP_CURRENCY_NOT_SUPPORTED,
                        "Flow target does not support any currencies",
                        HttpStatus.BAD_REQUEST);
                return ValidationResult.failure(errorResponse);
              }

              List<String> validationErrors = new java.util.ArrayList<>();

              for (OperationCurrencyValidationRequest.PspOperation operation :
                  request.getOperations()) {
                if (operation.getCurrencies() != null) {
                  for (OperationCurrencyValidationRequest.CurrencyInfo currency :
                      operation.getCurrencies()) {
                    String currencyCode = currency.getCurrency();
                    if (!supportedCurrencies.contains(currencyCode)) {
                      validationErrors.add(
                          String.format(
                              "Currency '%s' in flow action '%s' is not supported by flow target '%s'. Supported currencies: %s",
                              currencyCode,
                              operation.getFlowActionId(),
                              request.getFlowTargetId(),
                              String.join(", ", supportedCurrencies)));
                    }
                  }
                }
              }

              if (!validationErrors.isEmpty()) {
                String errorMessage =
                    "Currency validation failed:\n" + String.join("\n", validationErrors);
                ResponseEntity<ApiResponse<Object>> errorResponse =
                    responseHandler.errorResponse(
                        ErrorCode.PSP_CURRENCY_NOT_SUPPORTED, errorMessage, HttpStatus.BAD_REQUEST);
                return ValidationResult.failure(errorResponse);
              }

              return ValidationResult.success();
            });
  }
}
