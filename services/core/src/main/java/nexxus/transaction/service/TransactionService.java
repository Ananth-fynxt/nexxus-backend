package nexxus.transaction.service;

import org.springframework.http.ResponseEntity;

import nexxus.shared.dto.ApiResponse;
import nexxus.shared.service.BaseService;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionLogDto;

import reactor.core.publisher.Mono;

public interface TransactionService extends BaseService {

  Mono<ResponseEntity<ApiResponse<Object>>> create(TransactionDto transactionDto);

  Mono<ResponseEntity<ApiResponse<Object>>> getById(String id);

  Mono<ResponseEntity<ApiResponse<Object>>> createTransactionLog(
      TransactionLogDto transactionLogDto);
}
