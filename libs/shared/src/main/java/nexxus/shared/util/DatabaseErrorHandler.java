package nexxus.shared.util;

import java.sql.SQLException;
import java.util.regex.Pattern;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.r2dbc.BadSqlGrammarException;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.exception.BusinessException;

/**
 * Comprehensive utility class for handling database errors and converting them to user-friendly
 * messages without exposing internal SQL details or constraint names.
 */
public class DatabaseErrorHandler {

  // Patterns for sanitizing SQL constraint names and technical details
  private static final Pattern CONSTRAINT_NAME_PATTERN =
      Pattern.compile("constraint \"[^\"]+\"", Pattern.CASE_INSENSITIVE);
  private static final Pattern TABLE_NAME_PATTERN =
      Pattern.compile("table \"[^\"]+\"", Pattern.CASE_INSENSITIVE);
  private static final Pattern COLUMN_NAME_PATTERN =
      Pattern.compile("column \"[^\"]+\"", Pattern.CASE_INSENSITIVE);
  private static final Pattern SQL_STATE_PATTERN =
      Pattern.compile("\\bSQL state: \\w+", Pattern.CASE_INSENSITIVE);

  /**
   * Sanitizes database error messages to remove SQL details and constraint names
   *
   * @param throwable The database exception
   * @return User-friendly error message
   */
  public static String sanitizeErrorMessage(Throwable throwable) {
    if (throwable == null) {
      return ErrorCode.DATABASE_OPERATION_FAILED.getMessage();
    }

    String originalMessage = throwable.getMessage();
    if (originalMessage == null) {
      return ErrorCode.DATABASE_OPERATION_FAILED.getMessage();
    }

    // Convert to lowercase for case-insensitive matching
    String lowerMessage = originalMessage.toLowerCase();

    // Handle specific Spring Data exceptions first
    if (throwable instanceof DuplicateKeyException) {
      return ErrorCode.DUPLICATE_RESOURCE.getMessage();
    }

    if (throwable instanceof EmptyResultDataAccessException) {
      return ErrorCode.RESOURCE_NOT_FOUND.getMessage();
    }

    if (throwable instanceof IncorrectResultSizeDataAccessException) {
      return ErrorCode.MULTIPLE_RESULTS.getMessage();
    }

    if (throwable instanceof QueryTimeoutException) {
      return ErrorCode.DATABASE_OPERATION_TIMEOUT.getMessage();
    }

    if (throwable instanceof BadSqlGrammarException) {
      return ErrorCode.DATABASE_QUERY_ERROR.getMessage();
    }

    if (throwable instanceof InvalidDataAccessApiUsageException) {
      return ErrorCode.INVALID_DATABASE_OPERATION.getMessage();
    }

    // Handle unique constraint violations
    if (lowerMessage.contains("unique constraint")
        || lowerMessage.contains("duplicate key")
        || lowerMessage.contains("unique violation")
        || lowerMessage.contains("already exists")) {
      return ErrorCode.DUPLICATE_RESOURCE.getMessage();
    }

    // Handle foreign key constraint violations
    if (lowerMessage.contains("foreign key constraint")
        || lowerMessage.contains("violates foreign key")
        || lowerMessage.contains("fk constraint")
        || lowerMessage.contains("referenced")) {
      return ErrorCode.DATABASE_FOREIGN_KEY_VIOLATION.getMessage();
    }

    // Handle not null constraint violations
    if (lowerMessage.contains("not null constraint")
        || lowerMessage.contains("not-null constraint")
        || lowerMessage.contains("null value")
        || lowerMessage.contains("cannot be null")) {
      return ErrorCode.DATABASE_REQUIRED_DATA_MISSING.getMessage();
    }

    // Handle check constraint violations
    if (lowerMessage.contains("check constraint")
        || lowerMessage.contains("check violation")
        || lowerMessage.contains("value out of range")) {
      return ErrorCode.DATABASE_INVALID_DATA.getMessage();
    }

    // Handle connection issues
    if (lowerMessage.contains("connection")
        && (lowerMessage.contains("refused")
            || lowerMessage.contains("timeout")
            || lowerMessage.contains("closed")
            || lowerMessage.contains("reset")
            || lowerMessage.contains("lost"))) {
      return ErrorCode.DATABASE_CONNECTION_ISSUE.getMessage();
    }

    // Handle transaction issues
    if (lowerMessage.contains("transaction")
        && (lowerMessage.contains("deadlock")
            || lowerMessage.contains("rollback")
            || lowerMessage.contains("serialization"))) {
      return ErrorCode.DATABASE_TRANSACTION_CONFLICT.getMessage();
    }

    // Handle syntax errors (shouldn't happen in production, but just in case)
    if (lowerMessage.contains("syntax error")
        || lowerMessage.contains("sql syntax")
        || lowerMessage.contains("parse error")
        || lowerMessage.contains("invalid sql")) {
      return ErrorCode.DATABASE_QUERY_ERROR.getMessage();
    }

    // Handle table/column not found errors
    if (lowerMessage.contains("does not exist")
        && (lowerMessage.contains("table")
            || lowerMessage.contains("column")
            || lowerMessage.contains("relation"))) {
      return ErrorCode.DATABASE_STRUCTURE_ERROR.getMessage();
    }

    // Handle permission issues
    if (lowerMessage.contains("permission denied")
        || lowerMessage.contains("access denied")
        || lowerMessage.contains("insufficient privileges")) {
      return ErrorCode.DATABASE_ACCESS_DENIED.getMessage();
    }

    // Handle disk space issues
    if (lowerMessage.contains("disk")
        && (lowerMessage.contains("full") || lowerMessage.contains("space"))) {
      return ErrorCode.DATABASE_STORAGE_ISSUE.getMessage();
    }

    // Generic database error fallback
    return ErrorCode.DATABASE_OPERATION_FAILED.getMessage();
  }

  /**
   * Determines the appropriate ErrorCode based on the database exception
   *
   * @param throwable The database exception
   * @return Appropriate ErrorCode
   */
  public static ErrorCode getErrorCode(Throwable throwable) {
    if (throwable == null) {
      return ErrorCode.DATABASE_ERROR;
    }

    // Handle specific Spring Data exceptions first
    if (throwable instanceof DuplicateKeyException) {
      return ErrorCode.DUPLICATE_RESOURCE;
    }

    if (throwable instanceof EmptyResultDataAccessException) {
      return ErrorCode.RESOURCE_NOT_FOUND;
    }

    if (throwable instanceof IncorrectResultSizeDataAccessException) {
      return ErrorCode.MULTIPLE_RESULTS;
    }

    if (throwable instanceof QueryTimeoutException) {
      return ErrorCode.SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof BadSqlGrammarException
        || throwable instanceof InvalidDataAccessApiUsageException) {
      return ErrorCode.DATABASE_ERROR;
    }

    String originalMessage = throwable.getMessage();
    if (originalMessage == null) {
      return ErrorCode.DATABASE_ERROR;
    }

    String lowerMessage = originalMessage.toLowerCase();

    // Handle unique constraint violations
    if (lowerMessage.contains("unique constraint")
        || lowerMessage.contains("duplicate key")
        || lowerMessage.contains("unique violation")
        || lowerMessage.contains("already exists")) {
      return ErrorCode.DUPLICATE_RESOURCE;
    }

    // Handle foreign key constraint violations
    if (lowerMessage.contains("foreign key constraint")
        || lowerMessage.contains("violates foreign key")
        || lowerMessage.contains("fk constraint")
        || lowerMessage.contains("referenced")) {
      return ErrorCode.CONFLICT;
    }

    // Handle not null constraint violations
    if (lowerMessage.contains("not null constraint")
        || lowerMessage.contains("not-null constraint")
        || lowerMessage.contains("null value")
        || lowerMessage.contains("cannot be null")) {
      return ErrorCode.VALIDATION_ERROR;
    }

    // Handle check constraint violations
    if (lowerMessage.contains("check constraint")
        || lowerMessage.contains("check violation")
        || lowerMessage.contains("value out of range")) {
      return ErrorCode.VALIDATION_ERROR;
    }

    // Handle connection and transaction issues
    if ((lowerMessage.contains("connection")
            && (lowerMessage.contains("refused")
                || lowerMessage.contains("timeout")
                || lowerMessage.contains("closed")))
        || (lowerMessage.contains("transaction") && lowerMessage.contains("deadlock"))) {
      return ErrorCode.SERVICE_UNAVAILABLE;
    }

    // Default to generic database error
    return ErrorCode.DATABASE_ERROR;
  }

  /**
   * Checks if the exception is a DataIntegrityViolationException
   *
   * @param throwable The exception to check
   * @return true if it's a data integrity violation
   */
  public static boolean isDataIntegrityViolation(Throwable throwable) {
    return throwable instanceof DataIntegrityViolationException;
  }

  /**
   * Creates a sanitized BusinessException from a database exception
   *
   * @param throwable The database exception
   * @param contextMessage Additional context for the error
   * @return BusinessException with sanitized message
   */
  public static BusinessException createSanitizedException(
      Throwable throwable, String contextMessage) {
    ErrorCode errorCode = getErrorCode(throwable);
    String sanitizedMessage = sanitizeErrorMessage(throwable);

    if (contextMessage != null && !contextMessage.trim().isEmpty()) {
      sanitizedMessage = contextMessage + ": " + sanitizedMessage;
    }

    return new BusinessException(errorCode, sanitizedMessage, throwable);
  }

  /**
   * Creates a sanitized BusinessException from a database exception
   *
   * @param throwable The database exception
   * @return BusinessException with sanitized message
   */
  public static BusinessException createSanitizedException(Throwable throwable) {
    return createSanitizedException(throwable, null);
  }

  /**
   * Sanitizes any SQL technical details from error messages using regex patterns
   *
   * @param message The original error message
   * @return Sanitized message with technical details removed
   */
  public static String sanitizeSqlDetails(String message) {
    if (message == null) {
      return ErrorCode.DATABASE_OPERATION_FAILED.getMessage();
    }

    String sanitized = message;

    // Remove constraint names
    sanitized = CONSTRAINT_NAME_PATTERN.matcher(sanitized).replaceAll("constraint");

    // Remove table names
    sanitized = TABLE_NAME_PATTERN.matcher(sanitized).replaceAll("table");

    // Remove column names
    sanitized = COLUMN_NAME_PATTERN.matcher(sanitized).replaceAll("column");

    // Remove SQL state information
    sanitized = SQL_STATE_PATTERN.matcher(sanitized).replaceAll("");

    // Clean up extra spaces
    sanitized = sanitized.replaceAll("\\s+", " ").trim();

    return sanitized.isEmpty() ? ErrorCode.DATABASE_OPERATION_FAILED.getMessage() : sanitized;
  }

  /**
   * Checks if the throwable is a database-related exception
   *
   * @param throwable The exception to check
   * @return true if it's a database exception
   */
  public static boolean isDatabaseException(Throwable throwable) {
    return throwable instanceof DataAccessException
        || throwable instanceof SQLException
        || (throwable != null && throwable.getClass().getSimpleName().contains("R2dbc"));
  }
}
