package nexxus.shared.constants;

/**
 * Centralized error codes for the Nexxus application Each error code follows the pattern:
 * MODULE_OPERATION_ERROR
 */
public enum ErrorCode {
  // General/Common Errors (1000-1099)
  GENERIC_ERROR("1000", "An unexpected error occurred"),
  VALIDATION_ERROR("1001", "Validation failed"),
  INVALID_REQUEST("1002", "Invalid request format"),
  RESOURCE_NOT_FOUND("1003", "Requested resource not found"),
  UNAUTHORIZED("1004", "Unauthorized access"),
  FORBIDDEN("1005", "Access forbidden"),
  CONFLICT("1006", "Resource conflict"),
  RATE_LIMIT_EXCEEDED("1007", "Rate limit exceeded"),
  SERVICE_UNAVAILABLE("1008", "Service temporarily unavailable"),
  EXTERNAL_SERVICE_ERROR("1009", "External service error"),
  DUPLICATE_RESOURCE("1010", "Resource already exists"),
  MULTIPLE_RESULTS("1011", "Multiple resources found with the same criteria"),
  MISSING_REQUIRED_PARAMETER("1012", "Missing required parameter"),
  INVALID_REQUEST_BODY("1013", "Invalid request body format"),
  REQUEST_BODY_REQUIRED("1014", "Request body is required but was not provided"),
  VALIDATION_FAILED_MISSING_PARAMETERS("1015", "Validation failed. Missing or invalid parameters"),
  REQUEST_METHOD_NOT_SUPPORTED("1016", "Request method not supported"),
  INVALID_REQUEST_FORMAT("1017", "Invalid request format"),
  INVALID_JSON_FORMAT("1018", "Invalid JSON format in request body"),
  INVALID_JSON_SYNTAX("1019", "Invalid JSON syntax in request body"),
  ACCESS_DENIED("1020", "Access denied"),
  AUTHENTICATION_FAILED("1021", "Authentication failed"),
  INVALID_CREDENTIALS("1022", "Invalid credentials"),
  UNEXPECTED_ERROR("1023", "An unexpected error occurred. Please try again later."),

  // Database Errors (1100-1199)
  DATABASE_ERROR("1100", "Database operation failed"),
  DATABASE_QUERY_ERROR("1101", "Database query error"),
  DATABASE_STRUCTURE_ERROR("1102", "Database structure error"),
  MISSING_DATABASE_ENV_VARS("1103", "Missing required database environment variables"),
  DATABASE_OPERATION_FAILED("1104", "Database operation failed"),
  DATABASE_OPERATION_TIMEOUT("1105", "Database operation timed out"),
  INVALID_DATABASE_OPERATION("1106", "Invalid database operation"),
  DATABASE_FOREIGN_KEY_VIOLATION(
      "1107", "Cannot perform operation: Referenced data does not exist or is still in use"),
  DATABASE_REQUIRED_DATA_MISSING("1108", "Required data is missing"),
  DATABASE_INVALID_DATA("1109", "Invalid data provided"),
  DATABASE_CONNECTION_ISSUE("1110", "Database connection issue"),
  DATABASE_TRANSACTION_CONFLICT("1111", "Database transaction conflict"),
  DATABASE_ACCESS_DENIED("1112", "Database access denied"),
  DATABASE_STORAGE_ISSUE("1113", "Database storage issue"),
  DATABASE_CONSTRAINT_VIOLATION("1114", "Database constraint violation"),
  DATABASE_SYNTAX_ERROR("1115", "Database syntax error"),
  DATABASE_TABLE_NOT_FOUND("1116", "Database table not found"),
  DATABASE_COLUMN_NOT_FOUND("1117", "Database column not found"),
  DATABASE_RELATION_NOT_FOUND("1118", "Database relation not found"),
  DATABASE_PERMISSION_DENIED("1119", "Database permission denied"),
  DATABASE_INSUFFICIENT_PRIVILEGES("1120", "Database insufficient privileges"),
  DATABASE_DISK_FULL("1121", "Database disk full"),
  DATABASE_DISK_SPACE_ISSUE("1122", "Database disk space issue"),

  // Authentication & Authorization Errors (1200-1299)
  AUTH_INVALID_CREDENTIALS("1200", "Invalid credentials"),
  AUTH_TOKEN_EXPIRED("1201", "Authentication token expired"),
  AUTH_TOKEN_INVALID("1202", "Invalid authentication token"),
  AUTH_TOKEN_MISSING("1203", "Authentication token missing"),
  AUTH_INSUFFICIENT_PERMISSIONS("1204", "Insufficient permissions"),
  AUTH_ACCOUNT_LOCKED("1205", "Account is locked"),
  AUTH_ACCOUNT_DISABLED("1206", "Account is disabled"),
  AUTH_PASSWORD_EXPIRED("1207", "Password has expired"),
  AUTH_REFRESH_TOKEN_INVALID("1208", "Invalid refresh token"),
  AUTH_SESSION_EXPIRED("1209", "Session has expired"),

  // Brand Management Errors (1300-1399)
  BRAND_NOT_FOUND("1300", "Brand not found"),
  BRAND_ALREADY_EXISTS("1301", "Brand already exists"),
  BRAND_NAME_INVALID("1302", "Invalid brand name"),
  BRAND_STATUS_INVALID("1303", "Invalid brand status"),
  BRAND_PERMISSION_DENIED("1304", "Insufficient brand permissions"),

  // Environment Management Errors (1400-1499)
  ENVIRONMENT_NOT_FOUND("1400", "Environment not found"),
  ENVIRONMENT_ALREADY_EXISTS("1401", "Environment already exists"),
  ENVIRONMENT_NAME_INVALID("1402", "Invalid environment name"),
  ENVIRONMENT_STATUS_INVALID("1403", "Invalid environment status"),

  // Flow Management Errors (1500-1599)
  FLOW_ACTION_NOT_FOUND("1500", "Flow action not found"),
  FLOW_ACTION_ALREADY_EXISTS("1501", "Flow action already exists"),
  FLOW_ACTION_NAME_INVALID("1502", "Invalid flow action name"),
  FLOW_ACTION_INPUT_SCHEMA_REQUIRED("1503", "Input schema is required"),
  FLOW_ACTION_OUTPUT_SCHEMA_REQUIRED("1504", "Output schema is required"),

  FLOW_TARGET_NOT_FOUND("1510", "Flow target not found"),
  FLOW_TARGET_ALREADY_EXISTS("1511", "Flow target already exists"),
  FLOW_TARGET_NAME_INVALID("1512", "Invalid flow target name"),
  FLOW_TARGET_LOGO_REQUIRED("1513", "Flow target logo is required"),
  FLOW_TARGET_STATUS_REQUIRED("1514", "Flow target status is required"),
  FLOW_TARGET_CREDENTIAL_SCHEMA_REQUIRED("1515", "Credential schema is required"),

  FLOW_TYPE_NOT_FOUND("1520", "Flow type not found"),
  FLOW_TYPE_ALREADY_EXISTS("1521", "Flow type already exists"),
  FLOW_TYPE_NAME_INVALID("1522", "Invalid flow type name"),

  FLOW_DEFINITION_NOT_FOUND("1530", "Flow definition not found"),
  FLOW_DEFINITION_ALREADY_EXISTS("1531", "Flow definition already exists"),
  FLOW_DEFINITION_CODE_REQUIRED("1532", "Code is required"),
  FLOW_DEFINITION_ACTION_ID_REQUIRED("1533", "Flow action ID is required"),
  FLOW_DEFINITION_TARGET_ID_REQUIRED("1534", "Flow target ID is required"),

  // Payment & PSP Errors (1600-1699)
  PSP_NOT_FOUND("1600", "Payment service provider not found"),
  PSP_ALREADY_EXISTS("1601", "Payment service provider already exists"),
  PSP_CONFIGURATION_ERROR("1602", "PSP configuration error"),
  PSP_CONNECTION_ERROR("1603", "PSP connection failed"),
  PSP_TRANSACTION_FAILED("1604", "PSP transaction failed"),
  PSP_INVALID_RESPONSE("1605", "Invalid PSP response"),
  PSP_TIMEOUT("1606", "PSP request timeout"),
  PSP_RATE_LIMIT("1607", "PSP rate limit exceeded"),
  PSP_MAINTENANCE_MODE("1608", "PSP is in maintenance mode"),
  PSP_SERVICE_UNAVAILABLE("1609", "PSP service unavailable"),
  PSP_INVALID_CREDENTIALS("1610", "Invalid PSP credentials"),
  PSP_ACCOUNT_SUSPENDED("1611", "PSP account suspended"),
  PSP_CURRENCY_NOT_SUPPORTED("1612", "Currency not supported by PSP"),
  PSP_TRANSACTION_TYPE_NOT_SUPPORTED("1613", "Transaction type not supported by PSP"),
  PSP_AMOUNT_LIMIT_EXCEEDED("1614", "Amount limit exceeded for PSP"),
  PSP_GEO_RESTRICTION("1615", "PSP geo-restriction applied"),
  PSP_FRAUD_DETECTION("1616", "PSP fraud detection triggered"),
  PSP_SETTLEMENT_FAILED("1617", "PSP settlement failed"),
  PSP_RECONCILIATION_ERROR("1618", "PSP reconciliation error"),
  PSP_WEBHOOK_FAILED("1619", "PSP webhook delivery failed"),
  PSP_NAME_REQUIRED("1620", "PSP name is required"),
  PSP_CREDENTIAL_REQUIRED("1621", "Credential is required"),
  PSP_BRAND_ID_REQUIRED("1622", "Brand ID is required"),
  PSP_ENVIRONMENT_ID_REQUIRED("1623", "Environment ID is required"),
  PSP_FLOW_TARGET_ID_REQUIRED("1624", "Flow target ID is required"),

  // Fee Management Errors (1700-1799)
  FEE_NOT_FOUND("1700", "Fee configuration not found"),
  FEE_ALREADY_EXISTS("1701", "Fee configuration already exists"),
  FEE_INVALID_AMOUNT("1702", "Invalid fee amount"),
  FEE_INVALID_PERCENTAGE("1703", "Invalid fee percentage"),
  FEE_CONFIGURATION_ERROR("1704", "Fee configuration error"),
  FEE_NAME_REQUIRED("1705", "Fee name is required"),
  FEE_CURRENCY_REQUIRED("1706", "Currency is required"),
  FEE_CHARGE_FEE_TYPE_REQUIRED("1707", "Charge fee type is required"),
  FEE_BRAND_ID_REQUIRED("1708", "Brand ID is required"),
  FEE_ENVIRONMENT_ID_REQUIRED("1709", "Environment ID is required"),
  FEE_FLOW_ACTION_ID_REQUIRED("1710", "Flow Action ID is required"),
  FEE_COMPONENTS_REQUIRED("1711", "At least one component is required"),
  FEE_COUNTRIES_REQUIRED("1712", "At least one country is required"),
  FEE_PSPS_REQUIRED("1713", "At least one PSP is required"),
  FEE_COMPONENT_TYPE_REQUIRED("1714", "Component type is required"),
  FEE_COMPONENT_AMOUNT_REQUIRED("1715", "Component amount is required"),

  // Conversion Rate Management Errors (1800-1899)
  CONVERSION_RATE_NOT_FOUND("1800", "Conversion rate not found"),
  CONVERSION_RATE_EXPIRED("1801", "Conversion rate has expired"),
  CONVERSION_RATE_INVALID("1802", "Invalid conversion rate"),
  CONVERSION_SOURCE_ERROR("1803", "Conversion rate source error"),
  CONVERSION_MARKUP_ERROR("1804", "Conversion markup configuration error"),
  CONVERSION_CURRENCY_PAIR_INVALID("1805", "Invalid currency pair for conversion"),
  CONVERSION_RATE_CALCULATION_ERROR("1806", "Error calculating conversion rate"),
  CONVERSION_PROVIDER_UNAVAILABLE("1807", "Conversion rate provider unavailable"),
  CONVERSION_LIMIT_EXCEEDED("1808", "Conversion limit exceeded"),
  CONVERSION_TRANSACTION_FAILED("1809", "Conversion transaction failed"),
  CONVERSION_SOURCE_TYPE_REQUIRED("1810", "Source Type is required"),
  CONVERSION_FETCH_OPTION_REQUIRED("1811", "Fetch option is required"),
  CONVERSION_BRAND_ID_REQUIRED("1812", "Brand ID is required"),
  CONVERSION_ENVIRONMENT_ID_REQUIRED("1813", "Environment ID is required"),
  CONVERSION_MARKUP_OPTION_REQUIRED("1814", "Markup Option is required"),
  CONVERSION_SOURCE_CURRENCY_REQUIRED("1815", "Source Currency is required"),
  CONVERSION_TARGET_CURRENCY_REQUIRED("1816", "Target Currency is required"),
  CONVERSION_AMOUNT_REQUIRED("1817", "Amount is required"),

  // Transaction Management Errors (1900-1999)
  TRANSACTION_NOT_FOUND("1900", "Transaction not found"),
  TRANSACTION_ALREADY_EXISTS("1901", "Transaction already exists"),
  TRANSACTION_INVALID_STATUS("1902", "Invalid transaction status"),
  TRANSACTION_AMOUNT_INVALID("1903", "Invalid transaction amount"),
  TRANSACTION_CURRENCY_INVALID("1904", "Invalid transaction currency"),
  TRANSACTION_PSP_UNAVAILABLE("1905", "PSP unavailable for transaction"),
  TRANSACTION_TIMEOUT("1906", "Transaction timeout"),
  TRANSACTION_DECLINED("1907", "Transaction declined"),
  TRANSACTION_PROCESSING_ERROR("1908", "Transaction processing error"),
  TRANSACTION_REVERSAL_FAILED("1909", "Transaction reversal failed"),
  TRANSACTION_REFUND_FAILED("1910", "Transaction refund failed"),
  TRANSACTION_SETTLEMENT_FAILED("1911", "Transaction settlement failed"),
  TRANSACTION_DUPLICATE("1912", "Duplicate transaction detected"),
  TRANSACTION_FRAUD_DETECTED("1913", "Fraud detected in transaction"),
  TRANSACTION_LIMIT_EXCEEDED("1914", "Transaction limit exceeded"),
  TRANSACTION_GEO_RESTRICTED("1915", "Transaction geo-restricted"),
  TRANSACTION_AMOUNT_REQUIRED("1916", "Amount is required"),
  TRANSACTION_CURRENCY_REQUIRED("1917", "Currency is required"),
  TRANSACTION_BRAND_ID_REQUIRED("1918", "Brand ID is required"),
  TRANSACTION_ENVIRONMENT_ID_REQUIRED("1919", "Environment ID is required"),
  TRANSACTION_FLOW_ACTION_ID_REQUIRED("1920", "Flow action ID is required"),
  TRANSACTION_USER_ATTRIBUTE_REQUIRED("1921", "User attribute is required"),
  TRANSACTION_LOG_TRANSACTION_ID_REQUIRED("1922", "Transaction ID is required"),
  TRANSACTION_LOG_DATA_REQUIRED("1923", "Log data is required"),

  // User Attribute Errors (2000-2099)
  USER_ATTRIBUTE_ID_REQUIRED("2000", "User ID is required"),
  USER_ATTRIBUTE_FIRST_NAME_REQUIRED("2001", "First name is required"),
  USER_ATTRIBUTE_LAST_NAME_REQUIRED("2002", "Last name is required"),
  USER_ATTRIBUTE_EMAIL_REQUIRED("2003", "Email is required"),
  USER_ATTRIBUTE_PHONE_REQUIRED("2004", "Phone is required"),
  USER_ATTRIBUTE_ADDRESS_REQUIRED("2005", "Address is required"),
  USER_ATTRIBUTE_ADDRESS_LINE1_REQUIRED("2006", "Address line1 is required"),
  USER_ATTRIBUTE_PHONE_CODE_REQUIRED("2007", "Phone code is required"),
  USER_ATTRIBUTE_PHONE_NUMBER_REQUIRED("2008", "Phone number is required"),

  // Risk Management Errors (2100-2199)
  RISK_RULE_NOT_FOUND("2100", "Risk rule not found"),
  RISK_RULE_ALREADY_EXISTS("2101", "Risk rule already exists"),
  RISK_RULE_INVALID("2102", "Invalid risk rule configuration"),
  RISK_THRESHOLD_EXCEEDED("2103", "Risk threshold exceeded"),
  RISK_SCORE_INVALID("2104", "Invalid risk score"),
  RISK_RULE_TYPE_REQUIRED("2105", "Type is required"),
  RISK_RULE_ACTION_REQUIRED("2106", "Action is required"),
  RISK_RULE_CURRENCY_REQUIRED("2107", "Currency is required"),
  RISK_RULE_DURATION_REQUIRED("2108", "Duration is required"),
  RISK_RULE_NAME_REQUIRED("2109", "Name is required"),
  RISK_RULE_MAX_AMOUNT_REQUIRED("2110", "Max amount is required"),
  RISK_RULE_BRAND_ID_REQUIRED("2111", "Brand ID is required"),
  RISK_RULE_ENVIRONMENT_ID_REQUIRED("2112", "Environment ID is required"),
  RISK_RULE_FLOW_ACTION_ID_REQUIRED("2113", "Flow Action ID is required"),
  RISK_RULE_PSPS_REQUIRED("2114", "PSPs list is required"),
  RISK_RULE_PSP_ID_REQUIRED("2115", "PSP ID is required"),
  RISK_RULE_CRITERIA_TYPE_REQUIRED("2116", "criteriaType is required when type is CUSTOMER"),
  RISK_RULE_CRITERIA_VALUE_REQUIRED("2117", "criteriaValue is required when type is CUSTOMER"),

  // Routing Errors (2200-2299)
  ROUTING_RULE_NOT_FOUND("2200", "Routing rule not found"),
  ROUTING_RULE_ALREADY_EXISTS("2201", "Routing rule already exists"),
  ROUTING_RULE_INVALID("2202", "Invalid routing rule configuration"),
  ROUTING_NO_AVAILABLE_PSP("2203", "No available PSP for routing"),
  ROUTING_CONDITION_INVALID("2204", "Invalid routing condition"),
  ROUTING_DEFAULT_RULE_DELETE_FORBIDDEN("2205", "Cannot delete default routing rule"),
  ROUTING_LAST_RULE_DELETE_FORBIDDEN("2206", "Cannot delete the last routing rule"),
  ROUTING_RULE_BRAND_ID_REQUIRED("2207", "Brand ID is required"),
  ROUTING_RULE_ENVIRONMENT_ID_REQUIRED("2208", "Environment ID is required"),
  ROUTING_RULE_PSP_SELECTION_MODE_REQUIRED("2209", "PSP selection mode is required"),
  ROUTING_RULE_CONDITION_JSON_REQUIRED("2210", "Condition JSON is required"),
  ROUTING_RULE_PSPS_REQUIRED("2211", "At least one PSP is required"),
  ROUTING_RULE_PSP_ID_REQUIRED("2212", "PSP ID is required"),

  // Webhook Errors (2300-2399)
  WEBHOOK_NOT_FOUND("2300", "Webhook not found"),
  WEBHOOK_ALREADY_EXISTS("2301", "Webhook already exists"),
  WEBHOOK_URL_INVALID("2302", "Invalid webhook URL"),
  WEBHOOK_DELIVERY_FAILED("2303", "Webhook delivery failed"),
  WEBHOOK_SIGNATURE_INVALID("2304", "Invalid webhook signature"),
  WEBHOOK_TIMEOUT("2305", "Webhook delivery timeout"),
  WEBHOOK_RETRY_EXCEEDED("2306", "Webhook retry limit exceeded"),
  WEBHOOK_INVALID_PAYLOAD("2307", "Invalid webhook payload"),
  WEBHOOK_ENDPOINT_UNREACHABLE("2308", "Webhook endpoint unreachable"),
  WEBHOOK_SSL_ERROR("2309", "Webhook SSL/TLS error"),
  WEBHOOK_RATE_LIMITED("2310", "Webhook rate limited by endpoint"),
  WEBHOOK_AUTHENTICATION_FAILED("2311", "Webhook authentication failed"),
  WEBHOOK_PROCESSING_ERROR("2312", "Webhook processing error"),
  WEBHOOK_VALIDATION_FAILED("2313", "Webhook validation failed"),
  WEBHOOK_DUPLICATE_EVENT("2314", "Duplicate webhook event received"),
  WEBHOOK_STATUS_TYPE_REQUIRED("2315", "Status type is required"),
  WEBHOOK_URL_REQUIRED("2316", "URL is required"),
  WEBHOOK_BRAND_ID_REQUIRED("2317", "Brand ID is required"),
  WEBHOOK_ENVIRONMENT_ID_REQUIRED("2318", "Environment ID is required"),
  WEBHOOK_RETRY_REQUIRED("2319", "Retry count is required"),
  WEBHOOK_STATUS_REQUIRED("2320", "Status is required"),

  // CRM Errors (2400-2499)
  CRM_CUSTOMER_NOT_FOUND("2400", "CRM customer not found"),
  CRM_CUSTOMER_ALREADY_EXISTS("2401", "CRM customer already exists"),
  CRM_CUSTOMER_DATA_INVALID("2402", "Invalid customer data"),
  CRM_SYNC_ERROR("2403", "CRM synchronization error"),

  // IP Management Errors (2500-2599)
  IP_ADDRESS_INVALID("2500", "Invalid IP address"),
  IP_RULE_NOT_FOUND("2501", "IP rule not found"),
  IP_RULE_ALREADY_EXISTS("2502", "IP rule already exists"),
  IP_RULE_INVALID("2503", "Invalid IP rule configuration"),
  IP_BLACKLISTED("2504", "IP address is blacklisted"),
  IP_WHITELIST_REQUIRED("2505", "IP address not in whitelist");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return code;
  }
}
