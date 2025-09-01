package nexxus.shared.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Service for validating credentials against flow target schemas */
@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialValidationService {

  private final SchemaValidator schemaValidator;

  /**
   * Validates credentials against a flow target's credential schema
   *
   * @param flowTargetId The flow target ID
   * @param credentialSchema The credential schema JSON string
   * @param credential The credential JSON string to validate
   * @throws SchemaValidationException if validation fails
   */
  public void validateCredential(String flowTargetId, String credentialSchema, String credential) {
    try {
      schemaValidator.validateAndThrow(credentialSchema, credential);
    } catch (SchemaValidationException e) {
      throw new SchemaValidationException(
          "Invalid credential for flow target: " + flowTargetId, e.getValidationErrors());
    }
  }
}
