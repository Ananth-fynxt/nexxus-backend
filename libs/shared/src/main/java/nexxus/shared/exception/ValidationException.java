package nexxus.shared.exception;

import java.util.Map;

import nexxus.shared.constants.ErrorCode;

import lombok.Getter;

/** Exception thrown when validation fails */
@Getter
public class ValidationException extends BusinessException {

  private final Map<String, String> fieldErrors;

  public ValidationException(String message) {
    super(ErrorCode.VALIDATION_ERROR, message);
    this.fieldErrors = null;
  }

  public ValidationException(String message, Map<String, String> fieldErrors) {
    super(ErrorCode.VALIDATION_ERROR, message, "Validation failed for one or more fields");
    this.fieldErrors = fieldErrors;
  }

  public ValidationException(String message, String details, Map<String, String> fieldErrors) {
    super(ErrorCode.VALIDATION_ERROR, message, details);
    this.fieldErrors = fieldErrors;
  }

  public ValidationException(ErrorCode errorCode, String message, Map<String, String> fieldErrors) {
    super(errorCode, message, "Validation failed for one or more fields");
    this.fieldErrors = fieldErrors;
  }
}
