package nexxus.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nexxus.shared.controller.BaseController;
import nexxus.shared.dto.ApiResponse;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionLogDto;
import nexxus.transaction.service.TransactionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController extends BaseController {

  private final TransactionService transactionService;

  @PostMapping
  public Mono<ResponseEntity<ApiResponse<Object>>> create(
      @RequestBody TransactionDto transactionDto) {
    return transactionService.create(transactionDto);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ApiResponse<Object>>> getById(@PathVariable("id") String id) {
    return transactionService.getById(id);
  }

  @PostMapping("/logs")
  public Mono<ResponseEntity<ApiResponse<Object>>> createTransactionLog(
      @RequestBody TransactionLogDto transactionLogDto) {
    return transactionService.createTransactionLog(transactionLogDto);
  }
}
