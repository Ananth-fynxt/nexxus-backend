package nexxus.shared.validation;

import java.util.List;

import lombok.Getter;

/** Exception thrown when schema validation fails */
@Getter
public class SchemaValidationException extends RuntimeException {

  private final List<String> validationErrors;

  public SchemaValidationException(String message, List<String> validationErrors) {
    super(message + ": " + String.join("; ", validationErrors));
    this.validationErrors = validationErrors;
  }

  public SchemaValidationException(String message, String validationError) {
    this(message, List.of(validationError));
  }

  public SchemaValidationException(String message) {
    this(message, List.of());
  }
}
