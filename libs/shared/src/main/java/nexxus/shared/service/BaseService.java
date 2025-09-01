package nexxus.shared.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.exception.BusinessException;
import nexxus.shared.exception.ResourceNotFoundException;
import nexxus.shared.exception.ValidationException;
import nexxus.shared.util.DatabaseErrorHandler;
import nexxus.shared.util.ReactiveResponseHandler;

import reactor.core.publisher.Mono;

/**
 * Base service interface that provides common error handling methods. Follows SOLID principles with
 * single responsibility for error handling.
 */
public interface BaseService {

  /** Get the response handler instance */
  ReactiveResponseHandler getResponseHandler();

  // ==================== VALIDATION METHODS ====================

  /** Validate that an object is not null */
  default <T> T validateNotNull(T object, String fieldName) {
    if (object == null) {
      throw new ValidationException(fieldName + " cannot be null");
    }
    return object;
  }

  /** Validate that a string is not blank */
  default String validateNotBlank(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new ValidationException(fieldName + " cannot be blank");
    }
    return value.trim();
  }

  /** Validate that a condition is true */
  default void validateCondition(boolean condition, String message) {
    if (!condition) {
      throw new ValidationException(message);
    }
  }

  // ==================== RESOURCE VALIDATION METHODS ====================

  /** Validate that a resource exists */
  default <T> T validateResourceExists(
      T resource, String resourceName, String fieldName, Object fieldValue) {
    if (resource == null) {
      throw new ResourceNotFoundException(resourceName, fieldName, fieldValue);
    }
    return resource;
  }

  /** Validate that a resource does not exist (for creation) */
  default void validateResourceNotExists(
      Object resource, String resourceName, String fieldName, Object fieldValue) {
    if (resource != null) {
      throw new BusinessException(
          ErrorCode.CONFLICT,
          String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue));
    }
  }

  // ==================== ERROR RESPONSE METHODS ====================

  /** Create a validation error response */
  default Mono<ResponseEntity<ApiResponse<Object>>> validationError(String message) {
    return Mono.just(getResponseHandler().validationError(message));
  }

  /** Create a not found error response */
  default Mono<ResponseEntity<ApiResponse<Object>>> notFoundError(
      String resourceName, String fieldName, Object fieldValue) {
    return Mono.just(getResponseHandler().notFoundError(resourceName, fieldName, fieldValue));
  }

  /** Create a not found error response with simple message */
  default Mono<ResponseEntity<ApiResponse<Object>>> notFoundError(String message) {
    return Mono.just(getResponseHandler().notFoundError(message));
  }

  /** Create a conflict error response */
  default Mono<ResponseEntity<ApiResponse<Object>>> conflictError(String message) {
    return Mono.just(getResponseHandler().conflictError(message));
  }

  /** Create a database error response */
  default Mono<ResponseEntity<ApiResponse<Object>>> databaseError(String message) {
    return Mono.just(getResponseHandler().databaseError(message));
  }

  /** Create a database error response from exception with sanitized message */
  default Mono<ResponseEntity<ApiResponse<Object>>> databaseError(Throwable throwable) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(throwable);
    ErrorCode errorCode = DatabaseErrorHandler.getErrorCode(throwable);

    // Use appropriate HTTP status based on error type
    HttpStatus status =
        switch (errorCode) {
          case DUPLICATE_RESOURCE -> HttpStatus.CONFLICT;
          case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
          case CONFLICT -> HttpStatus.CONFLICT;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

    return Mono.just(getResponseHandler().errorResponse(errorCode, sanitizedMessage, status));
  }

  /** Create a database error response from exception with custom operation context */
  default Mono<ResponseEntity<ApiResponse<Object>>> databaseError(
      Throwable throwable, String operation) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(throwable);
    ErrorCode errorCode = DatabaseErrorHandler.getErrorCode(throwable);

    // Add operation context to the message
    String contextualMessage = "Error " + operation + ": " + sanitizedMessage;

    // Use appropriate HTTP status based on error type
    HttpStatus status =
        switch (errorCode) {
          case DUPLICATE_RESOURCE -> HttpStatus.CONFLICT;
          case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
          case CONFLICT -> HttpStatus.CONFLICT;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

    return Mono.just(getResponseHandler().errorResponse(errorCode, contextualMessage, status));
  }

  /** Create a generic error response */
  default Mono<ResponseEntity<ApiResponse<Object>>> genericError(String message) {
    return Mono.just(getResponseHandler().genericError(message));
  }

  /** Create a custom error response */
  default Mono<ResponseEntity<ApiResponse<Object>>> customError(
      ErrorCode errorCode, String message, HttpStatus status) {
    return Mono.just(getResponseHandler().errorResponse(errorCode, message, status));
  }

  // ==================== SUCCESS RESPONSE METHODS ====================

  /** Create a success response with data */
  default Mono<ResponseEntity<ApiResponse<Object>>> successResponse(Object data, String message) {
    return Mono.just(getResponseHandler().successResponse(data, message));
  }

  /** Create a success response with data only */
  default Mono<ResponseEntity<ApiResponse<Object>>> successResponse(Object data) {
    return Mono.just(getResponseHandler().successResponse(data));
  }

  /** Create a success response with message only */
  default Mono<ResponseEntity<ApiResponse<Object>>> successResponse(String message) {
    return Mono.just(getResponseHandler().successResponse(message));
  }

  /** Create a success response with custom HTTP status */
  default Mono<ResponseEntity<ApiResponse<Object>>> successResponse(
      Object data, String message, HttpStatus status) {
    return Mono.just(getResponseHandler().successResponse(data, message, status));
  }
}
