package nexxus.shared.validation;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Result of schema validation */
@Getter
@AllArgsConstructor
public class ValidationResult {

  private final boolean valid;
  private final List<String> errors;

  /** Create a successful validation result */
  public static ValidationResult success() {
    return new ValidationResult(true, Collections.emptyList());
  }

  /** Create a failed validation result with errors */
  public static ValidationResult failure(List<String> errors) {
    return new ValidationResult(false, errors);
  }

  /** Create a failed validation result with a single error */
  public static ValidationResult failure(String error) {
    return new ValidationResult(false, List.of(error));
  }

  /** Check if validation was successful */
  public boolean isValid() {
    return valid;
  }

  /** Get the first error message */
  public String getFirstError() {
    return errors.isEmpty() ? null : errors.get(0);
  }

  /** Get all error messages as a single string */
  public String getErrorsAsString() {
    return String.join("; ", errors);
  }
}
