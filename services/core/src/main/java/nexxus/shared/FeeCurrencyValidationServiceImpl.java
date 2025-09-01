package nexxus.shared.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import nexxus.flowtarget.repository.FlowTargetRepository;
import nexxus.psp.repository.PspRepository;
import nexxus.psp.repository.SupportedCurrencyRepository;
import nexxus.shared.service.FeeCurrencyValidationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of fee currency validation service. Validates PSP currency support for fee
 * operations.
 */
@Service
@RequiredArgsConstructor
public class FeeCurrencyValidationServiceImpl implements FeeCurrencyValidationService {

  private final SupportedCurrencyRepository supportedCurrencyRepository;
  private final FlowTargetRepository flowTargetRepository;
  private final PspRepository pspRepository;

  @Override
  public Mono<Boolean> validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {
    return Flux.fromIterable(pspIds)
        .flatMap(
            pspId ->
                validateSinglePspCurrency(currency, pspId, brandId, environmentId, flowActionId))
        .all(isSupported -> isSupported);
  }

  /** Validates currency support for a single PSP */
  private Mono<Boolean> validateSinglePspCurrency(
      String currency, String pspId, String brandId, String environmentId, String flowActionId) {
    // First check supported_currencies table
    return supportedCurrencyRepository
        .findByCompositeKey(brandId, environmentId, flowActionId, pspId)
        .any(supportedCurrency -> currency.equals(supportedCurrency.getCurrency()))
        .flatMap(
            foundInSupportedCurrencies -> {
              if (foundInSupportedCurrencies) {
                return Mono.just(true);
              } else {
                // Fallback to flow_targets table
                return checkCurrencyInFlowTargets(currency, pspId);
              }
            });
  }

  /**
   * Checks if currency is supported in flow_targets table. PSP ID should match a flow target ID in
   * this context.
   */
  private Mono<Boolean> checkCurrencyInFlowTargets(String currency, String pspId) {
    return pspRepository
        .findById(pspId)
        .flatMap(
            psp ->
                flowTargetRepository
                    .findById(psp.getFlowTargetId())
                    .map(
                        flowTarget -> {
                          List<String> currencies = flowTarget.getCurrencies();
                          if (currencies != null && !currencies.isEmpty()) {
                            return currencies.contains(currency);
                          }
                          return false;
                        })
                    .defaultIfEmpty(false))
        .defaultIfEmpty(false);
  }

  @Override
  public Mono<List<String>> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {
    return Flux.fromIterable(pspIds)
        .filterWhen(
            pspId ->
                validateSinglePspCurrency(currency, pspId, brandId, environmentId, flowActionId)
                    .map(isSupported -> !isSupported))
        .collectList();
  }
}
