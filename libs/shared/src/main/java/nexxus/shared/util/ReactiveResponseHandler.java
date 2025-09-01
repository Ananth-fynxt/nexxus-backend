package nexxus.shared.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;

/**
 * Centralized response handler for reactive Spring WebFlux applications. Follows SOLID principles
 * with single responsibility for response handling.
 */
@Component
public class ReactiveResponseHandler {

  // ==================== SUCCESS RESPONSES ====================

  /** Create a successful response with data and custom message */
  public ResponseEntity<ApiResponse<Object>> successResponse(Object data, String message) {
    ApiResponse<Object> response =
        ApiResponse.<Object>builder()
            .success(true)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code("200")
            .message(message)
            .data(data)
            .build();
    return ResponseEntity.ok(response);
  }

  /** Create a successful response with data only */
  public ResponseEntity<ApiResponse<Object>> successResponse(Object data) {
    return successResponse(data, "Operation completed successfully");
  }

  /** Create a successful response with custom message only */
  public ResponseEntity<ApiResponse<Object>> successResponse(String message) {
    return successResponse(null, message);
  }

  /** Create a successful response with custom HTTP status */
  public ResponseEntity<ApiResponse<Object>> successResponse(
      Object data, String message, HttpStatus status) {
    ApiResponse<Object> response =
        ApiResponse.<Object>builder()
            .success(true)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code(String.valueOf(status.value()))
            .message(message)
            .data(data)
            .build();
    return ResponseEntity.status(status).body(response);
  }

  // ==================== ERROR RESPONSES WITH ERROR CODE ====================

  /** Create an error response using ErrorCode enum */
  public ResponseEntity<ApiResponse<Object>> errorResponse(ErrorCode errorCode, HttpStatus status) {
    ApiResponse<Object> response =
        ApiResponse.<Object>builder()
            .success(false)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .data(null)
            .build();
    return ResponseEntity.status(status).body(response);
  }

  /** Create an error response using ErrorCode with custom message */
  public ResponseEntity<ApiResponse<Object>> errorResponse(
      ErrorCode errorCode, String customMessage, HttpStatus status) {
    ApiResponse<Object> response =
        ApiResponse.<Object>builder()
            .success(false)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code(errorCode.getCode())
            .message(customMessage)
            .data(null)
            .build();
    return ResponseEntity.status(status).body(response);
  }

  /** Create an error response using ErrorCode with custom message and details */
  public ResponseEntity<ApiResponse<Object>> errorResponse(
      ErrorCode errorCode, String customMessage, String details, HttpStatus status) {
    ApiResponse<Object> response =
        ApiResponse.<Object>builder()
            .success(false)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code(errorCode.getCode())
            .message(customMessage)
            .data(null)
            .build();
    return ResponseEntity.status(status).body(response);
  }

  // ==================== CONVENIENCE ERROR METHODS ====================

  /** Create a validation error response */
  public ResponseEntity<ApiResponse<Object>> validationError(String message) {
    return errorResponse(ErrorCode.VALIDATION_ERROR, message, HttpStatus.BAD_REQUEST);
  }

  /** Create a resource not found error response */
  public ResponseEntity<ApiResponse<Object>> notFoundError(
      String resourceName, String fieldName, Object fieldValue) {
    String message = String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue);
    return errorResponse(ErrorCode.RESOURCE_NOT_FOUND, message, HttpStatus.NOT_FOUND);
  }

  /** Create a resource not found error response with simple message */
  public ResponseEntity<ApiResponse<Object>> notFoundError(String message) {
    return errorResponse(ErrorCode.RESOURCE_NOT_FOUND, message, HttpStatus.NOT_FOUND);
  }

  /** Create a conflict error response */
  public ResponseEntity<ApiResponse<Object>> conflictError(String message) {
    return errorResponse(ErrorCode.CONFLICT, message, HttpStatus.CONFLICT);
  }

  /** Create an unauthorized error response */
  public ResponseEntity<ApiResponse<Object>> unauthorizedError(String message) {
    return errorResponse(ErrorCode.UNAUTHORIZED, message, HttpStatus.UNAUTHORIZED);
  }

  /** Create a forbidden error response */
  public ResponseEntity<ApiResponse<Object>> forbiddenError(String message) {
    return errorResponse(ErrorCode.FORBIDDEN, message, HttpStatus.FORBIDDEN);
  }

  /** Create a database error response */
  public ResponseEntity<ApiResponse<Object>> databaseError(String message) {
    return errorResponse(ErrorCode.DATABASE_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /** Create a generic error response */
  public ResponseEntity<ApiResponse<Object>> genericError(String message) {
    return errorResponse(ErrorCode.GENERIC_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // ==================== LEGACY SUPPORT ====================

  /** Legacy method for backward compatibility */
  public ResponseEntity<ApiResponse<Object>> errorResponse(String message, int statusCode) {
    ApiResponse<Object> response =
        ApiResponse.<Object>builder()
            .success(false)
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code(String.valueOf(statusCode))
            .message(message)
            .data(null)
            .build();
    return ResponseEntity.status(HttpStatus.valueOf(statusCode)).body(response);
  }

  /** Legacy method for backward compatibility */
  public ResponseEntity<ApiResponse<Object>> errorResponse(String message) {
    return errorResponse(message, 400);
  }
}
