package nexxus.shared.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import lombok.extern.slf4j.Slf4j;

/** Schema validation utility for validating JSON payloads against JSON schemas */
@Slf4j
@Component
public class SchemaValidator {

  private final ObjectMapper objectMapper;
  private final JsonSchemaFactory schemaFactory;

  public SchemaValidator() {
    this.objectMapper = new ObjectMapper();
    this.schemaFactory = JsonSchemaFactory.byDefault();
  }

  /**
   * Validates a JSON payload against a JSON schema
   *
   * @param schemaJson JSON schema as string
   * @param payload JSON payload as string
   * @return ValidationResult containing validation status and errors
   */
  public ValidationResult validate(String schemaJson, String payload) {
    try {
      JsonNode schemaNode = objectMapper.readTree(schemaJson);
      JsonNode payloadNode = objectMapper.readTree(payload);

      JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
      ProcessingReport report = schema.validate(payloadNode);

      if (report.isSuccess()) {
        return ValidationResult.success();
      } else {
        List<String> errors = new ArrayList<>();
        report.forEach(
            processingMessage -> {
              if (processingMessage.getLogLevel().ordinal()
                  >= com.github.fge.jsonschema.core.report.LogLevel.ERROR.ordinal()) {
                errors.add(processingMessage.getMessage());
              }
            });
        return ValidationResult.failure(errors);
      }
    } catch (IOException e) {
      log.error("Error parsing JSON schema or payload", e);
      return ValidationResult.failure(List.of("Invalid JSON format: " + e.getMessage()));
    } catch (ProcessingException e) {
      log.error("Error processing JSON schema", e);
      return ValidationResult.failure(List.of("Invalid JSON schema: " + e.getMessage()));
    }
  }

  /**
   * Validates a JSON payload against a JSON schema and throws exception if validation fails
   *
   * @param schemaJson JSON schema as string
   * @param payload JSON payload as string
   * @throws SchemaValidationException if validation fails
   */
  public void validateAndThrow(String schemaJson, String payload) throws SchemaValidationException {
    ValidationResult result = validate(schemaJson, payload);
    if (!result.isValid()) {
      throw new SchemaValidationException("Schema validation failed", result.getErrors());
    }
  }
}
