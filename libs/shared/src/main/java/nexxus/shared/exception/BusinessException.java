package nexxus.shared.exception;

import nexxus.shared.constants.ErrorCode;

import lombok.Getter;

/**
 * Business exception for handling application-specific errors Uses centralized error codes for
 * consistent error handling
 */
@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String details;
  private final Object data;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = null;
    this.data = null;
  }

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.details = null;
    this.data = null;
  }

  public BusinessException(ErrorCode errorCode, String message, String details) {
    super(message);
    this.errorCode = errorCode;
    this.details = details;
    this.data = null;
  }

  public BusinessException(ErrorCode errorCode, String message, String details, Object data) {
    super(message);
    this.errorCode = errorCode;
    this.details = details;
    this.data = data;
  }

  public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.details = null;
    this.data = null;
  }

  public BusinessException(ErrorCode errorCode, String message, String details, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.details = details;
    this.data = null;
  }

  public BusinessException(
      ErrorCode errorCode, String message, String details, Object data, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.details = details;
    this.data = data;
  }
}
