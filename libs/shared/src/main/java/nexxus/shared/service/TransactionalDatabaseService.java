package nexxus.shared.service;

import java.util.function.Function;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.exception.BusinessException;
import nexxus.shared.util.DatabaseErrorHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Service for handling transactional database operations with proper error handling and rollback
 * capabilities. Provides reactive transaction management for create, update, and delete operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionalDatabaseService {

  private final DatabaseClient databaseClient;

  // ==================== CREATE OPERATIONS ====================

  /**
   * Executes a single create operation within a transaction
   *
   * @param operation The database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the created entity
   */
  @Transactional
  public <T> Mono<T> createInTransaction(
      Function<DatabaseClient, Mono<T>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(result -> log.debug("Successfully created: {}", operationName))
        .doOnError(error -> log.error("Error creating {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  /**
   * Executes a complex create operation with multiple related entities within a transaction
   *
   * @param operation The complex database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the result
   */
  @Transactional
  public <T> Mono<T> createComplexInTransaction(
      Function<DatabaseClient, Mono<T>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(
            result -> log.debug("Successfully completed complex create: {}", operationName))
        .doOnError(
            error -> log.error("Error in complex create {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  // ==================== UPDATE OPERATIONS ====================

  /**
   * Executes a single update operation within a transaction
   *
   * @param operation The database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the updated entity
   */
  @Transactional
  public <T> Mono<T> updateInTransaction(
      Function<DatabaseClient, Mono<T>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(result -> log.debug("Successfully updated: {}", operationName))
        .doOnError(error -> log.error("Error updating {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  /**
   * Executes a complex update operation with multiple related entities within a transaction
   *
   * @param operation The complex database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the result
   */
  @Transactional
  public <T> Mono<T> updateComplexInTransaction(
      Function<DatabaseClient, Mono<T>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(
            result -> log.debug("Successfully completed complex update: {}", operationName))
        .doOnError(
            error -> log.error("Error in complex update {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  // ==================== DELETE OPERATIONS ====================

  /**
   * Executes a single delete operation within a transaction
   *
   * @param operation The database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the deletion result
   */
  @Transactional
  public Mono<Integer> deleteInTransaction(
      Function<DatabaseClient, Mono<Integer>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(result -> log.debug("Successfully deleted {} rows: {}", result, operationName))
        .doOnError(error -> log.error("Error deleting {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  /**
   * Executes a complex delete operation with multiple related entities within a transaction
   *
   * @param operation The complex database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the result
   */
  @Transactional
  public <T> Mono<T> deleteComplexInTransaction(
      Function<DatabaseClient, Mono<T>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(
            result -> log.debug("Successfully completed complex delete: {}", operationName))
        .doOnError(
            error -> log.error("Error in complex delete {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  // ==================== READ OPERATIONS ====================

  /**
   * Executes a read operation within a transaction (useful for consistency)
   *
   * @param operation The database operation to execute
   * @param operationName Name of the operation for error context
   * @return Mono with the result
   */
  @Transactional(readOnly = true)
  public <T> Mono<T> readInTransaction(
      Function<DatabaseClient, Mono<T>> operation, String operationName) {
    return operation
        .apply(databaseClient)
        .doOnSuccess(result -> log.debug("Successfully read: {}", operationName))
        .doOnError(error -> log.error("Error reading {}: {}", operationName, error.getMessage()))
        .onErrorMap(this::handleDatabaseError);
  }

  // ==================== UTILITY METHODS ====================

  /**
   * Handles database errors and converts them to BusinessException with sanitized messages
   *
   * @param throwable The original exception
   * @return BusinessException with sanitized error message
   */
  private BusinessException handleDatabaseError(Throwable throwable) {
    if (throwable instanceof BusinessException) {
      return (BusinessException) throwable;
    }

    DatabaseErrorHandler.sanitizeErrorMessage(throwable);
    return DatabaseErrorHandler.createSanitizedException(throwable, "database operation");
  }

  /**
   * Validates that a database operation returned a result
   *
   * @param result The result to validate
   * @param operationName Name of the operation for error context
   * @return Mono with the validated result
   */
  public <T> Mono<T> validateResult(Mono<T> result, String operationName) {
    return result.switchIfEmpty(
        Mono.error(
            new BusinessException(
                ErrorCode.RESOURCE_NOT_FOUND, "No result found for operation: " + operationName)));
  }

  /**
   * Validates that a database operation affected the expected number of rows
   *
   * @param result The result to validate
   * @param expectedRows Expected number of affected rows
   * @param operationName Name of the operation for error context
   * @return Mono with the validated result
   */
  public Mono<Integer> validateAffectedRows(
      Mono<Integer> result, int expectedRows, String operationName) {
    return result.flatMap(
        affectedRows -> {
          if (affectedRows != expectedRows) {
            return Mono.error(
                new BusinessException(
                    ErrorCode.DATABASE_ERROR,
                    String.format(
                        "Expected %d rows to be affected, but %d were affected for operation: %s",
                        expectedRows, affectedRows, operationName)));
          }
          return Mono.just(affectedRows);
        });
  }
}
