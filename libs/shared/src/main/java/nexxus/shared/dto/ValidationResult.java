package nexxus.shared.dto;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Wrapper for validation results that can contain either success or error response. */
@Getter
@AllArgsConstructor
public class ValidationResult {
  private final boolean valid;
  private final Optional<ResponseEntity<ApiResponse<Object>>> errorResponse;

  /** Creates a successful validation result. */
  public static ValidationResult success() {
    return new ValidationResult(true, Optional.empty());
  }

  /** Creates a failed validation result with error response. */
  public static ValidationResult failure(ResponseEntity<ApiResponse<Object>> errorResponse) {
    return new ValidationResult(false, Optional.of(errorResponse));
  }

  /** Returns true if validation passed. */
  public boolean isValid() {
    return valid;
  }

  /** Returns the error response if validation failed. */
  public Optional<ResponseEntity<ApiResponse<Object>>> getErrorResponse() {
    return errorResponse;
  }
}
