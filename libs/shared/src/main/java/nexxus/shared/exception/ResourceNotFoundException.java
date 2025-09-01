package nexxus.shared.exception;

import nexxus.shared.constants.ErrorCode;

/** Exception thrown when a requested resource is not found */
public class ResourceNotFoundException extends BusinessException {

  public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue),
        String.format(
            "The requested %s with %s '%s' was not found in the system",
            resourceName, fieldName, fieldValue));
  }

  public ResourceNotFoundException(String resourceName) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        String.format("%s not found", resourceName),
        String.format("The requested %s was not found in the system", resourceName));
  }

  public ResourceNotFoundException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public ResourceNotFoundException(ErrorCode errorCode, String message, String details) {
    super(errorCode, message, details);
  }
}
