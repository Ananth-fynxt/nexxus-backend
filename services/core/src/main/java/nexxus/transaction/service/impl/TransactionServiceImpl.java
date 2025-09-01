package nexxus.transaction.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nexxus.routingrule.repository.RoutingRuleRepository;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.TransactionStatus;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.BusinessException;
import nexxus.shared.util.IdGenerator;
import nexxus.shared.util.ReactiveResponseHandler;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionLogDto;
import nexxus.transaction.dto.UserAttributeDto;
import nexxus.transaction.repository.CrmCustomerRepository;
import nexxus.transaction.repository.TransactionLogRepository;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository transactionRepository;
  private final CrmCustomerRepository crmCustomerRepository;
  private final TransactionLogRepository transactionLogRepository;
  private final RoutingRuleRepository routingRuleRepository;
  private final ObjectMapper objectMapper;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> create(TransactionDto transactionDto) {
    try {
      validateCreateRequest(transactionDto);

      return validateRoutingRule(transactionDto)
          .then(createTransaction(transactionDto))
          .onErrorResume(
              e -> {
                return databaseError(e, "creating transaction");
              });

    } catch (Exception e) {
      return databaseError(e, "creating transaction");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(String id) {
    try {
      validateNotBlank(id, "Transaction ID");

      return transactionRepository
          .findById(id)
          .flatMap(
              transaction -> {
                if (transaction == null) {
                  return customError(
                      ErrorCode.TRANSACTION_NOT_FOUND,
                      "Transaction not found with ID: " + id,
                      org.springframework.http.HttpStatus.NOT_FOUND);
                }

                try {
                  UserAttributeDto userAttribute =
                      objectMapper.readValue(
                          transaction.getUserAttribute().asString(), UserAttributeDto.class);

                  TransactionDto responseDto =
                      TransactionDto.builder()
                          .id(transaction.getId())
                          .amount(transaction.getAmount())
                          .currency(transaction.getCurrency())
                          .brandId(transaction.getBrandId())
                          .environmentId(transaction.getEnvironmentId())
                          .flowActionId(transaction.getFlowActionId())
                          .routingRuleId(transaction.getRoutingRuleId())
                          .userAttribute(userAttribute)
                          .status(transaction.getStatus())
                          .createdAt(transaction.getCreatedAt())
                          .build();

                  return successResponse(responseDto, "Transaction retrieved successfully");
                } catch (JsonProcessingException e) {
                  return databaseError(e, "processing transaction data");
                }
              })
          .switchIfEmpty(
              customError(
                  ErrorCode.TRANSACTION_NOT_FOUND,
                  "Transaction not found with ID: " + id,
                  org.springframework.http.HttpStatus.NOT_FOUND));

    } catch (Exception e) {
      return databaseError(e, "retrieving transaction");
    }
  }

  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> createTransactionLog(
      TransactionLogDto transactionLogDto) {
    try {
      validateCreateTransactionLogRequest(transactionLogDto);

      return createTransactionLogRecord(transactionLogDto)
          .onErrorResume(
              e -> {
                return databaseError(e, "creating transaction log");
              });

    } catch (Exception e) {
      return databaseError(e, "creating transaction log");
    }
  }

  private void validateCreateRequest(TransactionDto transactionDto) {
    validateNotNull(transactionDto, "Transaction DTO");
    validateNotNull(transactionDto.getAmount(), "Amount");
    validateNotBlank(transactionDto.getCurrency(), "Currency");
    validateNotBlank(transactionDto.getBrandId(), "Brand ID");
    validateNotBlank(transactionDto.getEnvironmentId(), "Environment ID");
    validateNotBlank(transactionDto.getFlowActionId(), "Flow action ID");
    validateNotNull(transactionDto.getUserAttribute(), "User attribute");
    validateNotBlank(transactionDto.getUserAttribute().getId(), "User attribute ID");
  }

  private void validateCreateTransactionLogRequest(TransactionLogDto transactionLogDto) {
    validateNotNull(transactionLogDto, "Transaction Log DTO");
    validateNotBlank(transactionLogDto.getTransactionId(), "Transaction ID");
    validateNotNull(transactionLogDto.getLog(), "Log data");
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createTransaction(
      TransactionDto transactionDto) {
    try {
      String userAttributeJson = objectMapper.writeValueAsString(transactionDto.getUserAttribute());

      String generatedId = IdGenerator.generateTransactionId();
      LocalDateTime now = LocalDateTime.now();

      UserAttributeDto userAttribute = transactionDto.getUserAttribute();

      String brandId = transactionDto.getBrandId();
      String environmentId = transactionDto.getEnvironmentId();

      return crmCustomerRepository
          .existsByBrandIdAndEnvironmentIdAndCrmCustomerId(
              brandId, environmentId, userAttribute.getId())
          .flatMap(
              exists -> {
                try {
                  if (exists == 0) {
                    String customAttributesJson = objectMapper.writeValueAsString(userAttribute);

                    return crmCustomerRepository
                        .insertCrmCustomer(
                            brandId,
                            environmentId,
                            userAttribute.getId(),
                            userAttribute.getFirstName() + " " + userAttribute.getLastName(),
                            userAttribute.getEmail(),
                            userAttribute.getTag(),
                            userAttribute.getAddress() != null
                                ? userAttribute.getAddress().getCountry()
                                : null,
                            userAttribute.getAccountType(),
                            customAttributesJson,
                            now)
                        .then(
                            createTransactionRecord(
                                transactionDto, generatedId, userAttributeJson, now));
                  } else {
                    return createTransactionRecord(
                        transactionDto, generatedId, userAttributeJson, now);
                  }
                } catch (JsonProcessingException e) {
                  return databaseError(e, "processing customer data");
                }
              })
          .onErrorResume(
              e -> {
                return databaseError(e, "creating transaction");
              });
    } catch (Exception e) {
      return databaseError(e, "creating transaction");
    }
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createTransactionRecord(
      TransactionDto transactionDto,
      String generatedId,
      String userAttributeJson,
      LocalDateTime now) {
    return transactionRepository
        .insertTransaction(
            generatedId,
            transactionDto.getAmount(),
            transactionDto.getCurrency(),
            transactionDto.getBrandId(),
            transactionDto.getEnvironmentId(),
            transactionDto.getFlowActionId(),
            transactionDto.getRoutingRuleId(),
            userAttributeJson,
            transactionDto.getStatus() != null
                ? transactionDto.getStatus()
                : TransactionStatus.INITIATED,
            now)
        .then(createTransactionLog(generatedId, now))
        .then(
            Mono.defer(
                () -> {
                  TransactionDto responseDto =
                      TransactionDto.builder()
                          .id(generatedId)
                          .amount(transactionDto.getAmount())
                          .currency(transactionDto.getCurrency())
                          .flowActionId(transactionDto.getFlowActionId())
                          .routingRuleId(transactionDto.getRoutingRuleId())
                          .userAttribute(transactionDto.getUserAttribute())
                          .status(
                              transactionDto.getStatus() != null
                                  ? transactionDto.getStatus()
                                  : TransactionStatus.INITIATED)
                          .createdAt(now)
                          .build();
                  return successResponse(responseDto, "Transaction created successfully");
                }));
  }

  private Mono<Void> validateRoutingRule(TransactionDto transactionDto) {
    if (transactionDto.getRoutingRuleId() != null
        && !transactionDto.getRoutingRuleId().trim().isEmpty()) {
      return routingRuleRepository
          .findLatestVersionById(transactionDto.getRoutingRuleId())
          .switchIfEmpty(
              Mono.error(
                  new BusinessException(
                      ErrorCode.ROUTING_RULE_NOT_FOUND,
                      "Routing rule not found with ID: " + transactionDto.getRoutingRuleId())))
          .flatMap(
              routingRule -> {
                if (!routingRule.getBrandId().equals(transactionDto.getBrandId())) {
                  return Mono.error(
                      new BusinessException(
                          ErrorCode.ROUTING_RULE_INVALID,
                          "Routing rule brand ID does not match transaction brand ID"));
                }
                if (!routingRule.getEnvironmentId().equals(transactionDto.getEnvironmentId())) {
                  return Mono.error(
                      new BusinessException(
                          ErrorCode.ROUTING_RULE_INVALID,
                          "Routing rule environment ID does not match transaction environment ID"));
                }
                return Mono.empty();
              });
    }
    return Mono.empty();
  }

  private Mono<Void> createTransactionLog(String transactionId, LocalDateTime now) {
    try {
      String logId = IdGenerator.generateTransactionLogId();

      String logData =
          objectMapper.writeValueAsString(
              Map.of(
                  "event",
                  "TRANSACTION_INITIATED",
                  "status",
                  TransactionStatus.INITIATED.getValue(),
                  "timestamp",
                  now.toString(),
                  "message",
                  "Transaction initiated successfully"));

      return transactionLogRepository.insertTransactionLog(
          logId, transactionId, null, null, logData, now);
    } catch (JsonProcessingException e) {
      return Mono.error(e);
    }
  }

  private Mono<ResponseEntity<ApiResponse<Object>>> createTransactionLogRecord(
      TransactionLogDto transactionLogDto) {
    try {
      String logId = IdGenerator.generateTransactionLogId();
      LocalDateTime now = LocalDateTime.now();
      String logJson = objectMapper.writeValueAsString(transactionLogDto.getLog());

      return transactionLogRepository
          .insertTransactionLog(
              logId,
              transactionLogDto.getTransactionId(),
              transactionLogDto.getPspId(),
              transactionLogDto.getWebhookId(),
              logJson,
              now)
          .then(
              Mono.defer(
                  () -> {
                    TransactionLogDto responseDto =
                        TransactionLogDto.builder()
                            .id(logId)
                            .transactionId(transactionLogDto.getTransactionId())
                            .pspId(transactionLogDto.getPspId())
                            .webhookId(transactionLogDto.getWebhookId())
                            .log(transactionLogDto.getLog())
                            .createdAt(now)
                            .build();
                    return successResponse(responseDto, "Transaction log created successfully");
                  }));
    } catch (JsonProcessingException e) {
      return databaseError(e, "processing log data");
    }
  }
}
