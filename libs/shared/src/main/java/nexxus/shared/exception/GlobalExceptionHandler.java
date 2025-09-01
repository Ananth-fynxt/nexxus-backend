package nexxus.shared.exception;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.DatabaseErrorHandler;
import nexxus.shared.util.ReactiveResponseHandler;

/**
 * Global exception handler that provides comprehensive error handling with ErrorCode integration.
 * Follows SOLID principles with single responsibility for exception handling.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final ReactiveResponseHandler responseHandler;

  public GlobalExceptionHandler(ReactiveResponseHandler responseHandler) {
    this.responseHandler = responseHandler;
  }

  // ==================== BUSINESS LOGIC EXCEPTIONS ====================

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
    return responseHandler.errorResponse(
        ex.getErrorCode(), ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    return responseHandler.errorResponse(
        ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex) {
    return responseHandler.errorResponse(
        ErrorCode.VALIDATION_ERROR, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  // ==================== VALIDATION EXCEPTIONS ====================

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
      WebExchangeBindException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    // Create detailed error message with missing fields
    StringBuilder messageBuilder = new StringBuilder();

    for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
      messageBuilder.append(entry.getKey()).append(" (").append(entry.getValue()).append("), ");
    }

    // Remove the last comma and space
    String message = messageBuilder.toString();
    if (message.endsWith(", ")) {
      message = message.substring(0, message.length() - 2);
    }

    return responseHandler.errorResponse(
        ErrorCode.VALIDATION_FAILED_MISSING_PARAMETERS, message, HttpStatus.BAD_REQUEST);
  }

  // ==================== HTTP EXCEPTIONS ====================

  @ExceptionHandler(MethodNotAllowedException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(
      MethodNotAllowedException ex) {
    return responseHandler.errorResponse(
        ErrorCode.REQUEST_METHOD_NOT_SUPPORTED,
        ErrorCode.REQUEST_METHOD_NOT_SUPPORTED.getMessage(),
        HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(ServerWebInputException.class)
  public ResponseEntity<ApiResponse<Object>> handleServerWebInput(ServerWebInputException ex) {
    String message = ErrorCode.INVALID_REQUEST_FORMAT.getMessage();

    // Try to extract more specific error information
    if (ex.getReason() != null) {
      message = "Invalid request: " + ex.getReason();
    } else if (ex.getMethodParameter() != null) {
      message = "Missing required parameter: " + ex.getMethodParameter().getParameterName();
    }

    return responseHandler.errorResponse(
        ErrorCode.MISSING_REQUIRED_PARAMETER, message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Object>> handleMessageNotReadable(
      HttpMessageNotReadableException ex) {
    String message = ErrorCode.INVALID_REQUEST_BODY.getMessage();

    // Try to provide more specific error information
    if (ex.getCause() != null && ex.getCause().getMessage() != null) {
      String causeMessage = ex.getCause().getMessage();
      if (causeMessage.contains("Required request body is missing")) {
        message = ErrorCode.REQUEST_BODY_REQUIRED.getMessage();
        return responseHandler.errorResponse(
            ErrorCode.REQUEST_BODY_REQUIRED, message, HttpStatus.BAD_REQUEST);
      } else if (causeMessage.contains("JSON parse error")) {
        message = ErrorCode.INVALID_JSON_FORMAT.getMessage();
      } else if (causeMessage.contains("Unexpected character")) {
        message = ErrorCode.INVALID_JSON_SYNTAX.getMessage();
      }
    }

    return responseHandler.errorResponse(
        ErrorCode.INVALID_REQUEST_BODY, message, HttpStatus.BAD_REQUEST);
  }

  // ==================== SECURITY EXCEPTIONS ====================

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
    return responseHandler.errorResponse(
        ErrorCode.FORBIDDEN, ErrorCode.ACCESS_DENIED.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse<Object>> handleAuthentication(AuthenticationException ex) {
    return responseHandler.errorResponse(
        ErrorCode.UNAUTHORIZED,
        ErrorCode.AUTHENTICATION_FAILED.getMessage(),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
    return responseHandler.errorResponse(
        ErrorCode.AUTH_INVALID_CREDENTIALS,
        ErrorCode.INVALID_CREDENTIALS.getMessage(),
        HttpStatus.UNAUTHORIZED);
  }

  // ==================== DATABASE EXCEPTIONS ====================

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    ErrorCode errorCode = DatabaseErrorHandler.getErrorCode(ex);
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);

    HttpStatus httpStatus =
        switch (errorCode) {
          case DUPLICATE_RESOURCE -> HttpStatus.CONFLICT;
          case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
          case CONFLICT -> HttpStatus.CONFLICT;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

    return responseHandler.errorResponse(errorCode, sanitizedMessage, httpStatus);
  }

  @ExceptionHandler(SQLException.class)
  public ResponseEntity<ApiResponse<Object>> handleSQLException(SQLException ex) {
    ErrorCode errorCode = DatabaseErrorHandler.getErrorCode(ex);
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);

    HttpStatus httpStatus =
        switch (errorCode) {
          case DUPLICATE_RESOURCE -> HttpStatus.CONFLICT;
          case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
          case CONFLICT -> HttpStatus.CONFLICT;
          case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

    return responseHandler.errorResponse(errorCode, sanitizedMessage, httpStatus);
  }

  @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
  public ResponseEntity<ApiResponse<Object>> handleIncorrectResultSize(
      IncorrectResultSizeDataAccessException ex) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);
    return responseHandler.errorResponse(
        ErrorCode.MULTIPLE_RESULTS, sanitizedMessage, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<ApiResponse<Object>> handleEmptyResult(EmptyResultDataAccessException ex) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);
    return responseHandler.errorResponse(
        ErrorCode.RESOURCE_NOT_FOUND, sanitizedMessage, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<ApiResponse<Object>> handleDuplicateKey(DuplicateKeyException ex) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);
    return responseHandler.errorResponse(
        ErrorCode.DUPLICATE_RESOURCE, sanitizedMessage, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(QueryTimeoutException.class)
  public ResponseEntity<ApiResponse<Object>> handleQueryTimeout(QueryTimeoutException ex) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);
    return responseHandler.errorResponse(
        ErrorCode.SERVICE_UNAVAILABLE, sanitizedMessage, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(BadSqlGrammarException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadSqlGrammar(BadSqlGrammarException ex) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);
    return responseHandler.errorResponse(
        ErrorCode.DATABASE_ERROR, sanitizedMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvalidDataAccessApiUsage(
      InvalidDataAccessApiUsageException ex) {
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);
    return responseHandler.errorResponse(
        ErrorCode.DATABASE_ERROR, sanitizedMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex) {
    ErrorCode errorCode = DatabaseErrorHandler.getErrorCode(ex);
    String sanitizedMessage = DatabaseErrorHandler.sanitizeErrorMessage(ex);

    HttpStatus httpStatus =
        switch (errorCode) {
          case DUPLICATE_RESOURCE -> HttpStatus.CONFLICT;
          case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
          case CONFLICT -> HttpStatus.CONFLICT;
          case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
          case MULTIPLE_RESULTS -> HttpStatus.CONFLICT;
          case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

    return responseHandler.errorResponse(errorCode, sanitizedMessage, httpStatus);
  }

  // ==================== SERVICE EXCEPTIONS ====================

  @ExceptionHandler(ServiceUnavailableException.class)
  public ResponseEntity<ApiResponse<Object>> handleServiceUnavailable(
      ServiceUnavailableException ex) {
    return responseHandler.errorResponse(
        ErrorCode.SERVICE_UNAVAILABLE,
        ErrorCode.SERVICE_UNAVAILABLE.getMessage(),
        HttpStatus.SERVICE_UNAVAILABLE);
  }

  // ==================== GENERIC EXCEPTION HANDLER ====================

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    return responseHandler.errorResponse(
        ErrorCode.GENERIC_ERROR,
        ErrorCode.UNEXPECTED_ERROR.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
