package nexxus.shared.util;

import java.security.SecureRandom;

import nexxus.shared.constants.IdPrefix;

/**
 * Utility class for generating TypeID-style IDs Based on the TypeScript typeid-js implementation
 */
public final class IdGenerator {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final String ALPHANUMERIC_CHARS =
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private IdGenerator() {
    // Utility class, prevent instantiation
  }

  /**
   * Generate a TypeID-style ID with the given prefix
   *
   * @param prefix the ID prefix (e.g., "usr" for users)
   * @return the generated ID
   */
  public static String generateId(String prefix) {
    if (prefix == null || prefix.isEmpty()) {
      return generateRandomId();
    }
    return prefix + "_" + generateRandomId();
  }

  /**
   * Generate a TypeID-style ID without the given prefix
   *
   * @param prefix the ID prefix (e.g., "usr" for users)
   * @return the generated ID
   */
  public static String generateIdWithoutPrefix(String prefix) {
    if (prefix == null || prefix.isEmpty()) {
      return generateRandomId();
    }
    return prefix + generateRandomId();
  }

  /**
   * Generate a brand ID
   *
   * @return the generated brand ID
   */
  public static String generateBrandId() {
    return generateId(IdPrefix.BRAND);
  }

  /**
   * Generate an environment ID
   *
   * @return the generated environment ID
   */
  public static String generateEnvironmentId() {
    return generateId(IdPrefix.ENVIRONMENT);
  }

  /**
   * Generate an environment secret ID
   *
   * @return the generated environment secret ID
   */
  public static String generateEnvironmentSecretId() {
    return generateId(IdPrefix.ENVIRONMENT_SECRET);
  }

  /**
   * Generate a flow type ID
   *
   * @return the generated flow type ID
   */
  public static String generateFlowTypeId() {
    return generateId(IdPrefix.FLOW_TYPE);
  }

  /**
   * Generate a flow action ID
   *
   * @return the generated flow action ID
   */
  public static String generateFlowActionId() {
    return generateId(IdPrefix.FLOW_ACTION);
  }

  /**
   * Generate a flow target ID
   *
   * @return the generated flow target ID
   */
  public static String generateFlowTargetId() {
    return generateId(IdPrefix.FLOW_TARGET);
  }

  /**
   * Generate a flow definition ID
   *
   * @return the generated flow definition ID
   */
  public static String generateFlowDefinitionId() {
    return generateId(IdPrefix.FLOW_DEFINITION);
  }

  /**
   * Generate a PSP ID
   *
   * @return the generated PSP ID
   */
  public static String generatePspId() {
    return generateId(IdPrefix.PSP);
  }

  /**
   * Generate a flow ID
   *
   * @return the generated flow ID
   */
  public static String generateFlowId() {
    return generateId(IdPrefix.FLOW);
  }

  /**
   * Generate a token ID (no prefix)
   *
   * @return the generated token ID
   */
  public static String generateTokenId() {
    return generateId(IdPrefix.TOKEN);
  }

  /**
   * Generate an IP rule ID
   *
   * @return the generated IP rule ID
   */
  public static String generateIpRuleId() {
    return generateId(IdPrefix.IP_RULE);
  }

  /**
   * Generate an IP rule entry ID
   *
   * @return the generated IP rule entry ID
   */
  public static String generateIpRuleEntryId() {
    return generateId(IdPrefix.IP_RULE_ENTRY);
  }

  /**
   * Generate a risk rule ID
   *
   * @return the generated risk rule ID
   */
  public static String generateRiskRuleId() {
    return generateId(IdPrefix.RISK_RULE);
  }

  /**
   * Generate a risk rule customer criteria ID
   *
   * @return the generated risk rule customer criteria ID
   */
  public static String generateRiskRuleCustomerCriteriaId() {
    return generateId(IdPrefix.RISK_RULE_CUSTOMER_CRITERIA);
  }

  /**
   * Generate an FX rate config ID
   *
   * @return the generated FX rate config ID
   */
  public static String generateFxRateConfigId() {
    return generateId(IdPrefix.FX_RATE_CONFIG);
  }

  /**
   * Generate an FX rate markup ID
   *
   * @return the generated FX rate markup ID
   */
  public static String generateFxRateMarkupId() {
    return generateId(IdPrefix.FX_RATE_MARKUP);
  }

  /**
   * Generate a conversion rate config ID
   *
   * @return the generated conversion rate config ID
   */
  public static String generateConversionRateId() {
    return generateId(IdPrefix.CONVERSION_RATE_CONFIG);
  }

  /**
   * Generate a conversion rate markup ID
   *
   * @return the generated conversion rate markup ID
   */
  public static String generateConversionRateMarkupId() {
    return generateId(IdPrefix.CONVERSION_RATE_MARKUP);
  }

  /**
   * Generate a fee ID
   *
   * @return the generated fee ID
   */
  public static String generateFeeId() {
    return generateId(IdPrefix.FEE);
  }

  /**
   * Generate a fee component ID
   *
   * @return the generated fee component ID
   */
  public static String generateFeeComponentId() {
    return generateId(IdPrefix.FEE_COMPONENT);
  }

  /**
   * Generate a maintenance window ID
   *
   * @return the generated maintenance window ID
   */
  public static String generateMaintenanceWindowId() {
    return generateId(IdPrefix.MAINTENANCE_WINDOW);
  }

  /**
   * Generate a transaction ID
   *
   * @return the generated transaction ID
   */
  public static String generateTransactionId() {
    return generateIdWithoutPrefix(IdPrefix.TRANSACTION);
  }

  /**
   * Generate a transaction log ID
   *
   * @return the generated transaction log ID
   */
  public static String generateTransactionLogId() {
    return generateIdWithoutPrefix(IdPrefix.TRANSACTION_LOG);
  }

  /**
   * Generate a webhook ID
   *
   * @return the generated webhook ID
   */
  public static String generateWebhookId() {
    return generateId(IdPrefix.WEBHOOK);
  }

  /**
   * Generate a webhook log ID
   *
   * @return the generated webhook log ID
   */
  public static String generateWebhookLogId() {
    return generateId(IdPrefix.WEBHOOK_LOG);
  }

  /**
   * Generate a routing rule ID
   *
   * @return the generated routing rule ID
   */
  public static String generateRoutingRuleId() {
    return generateId(IdPrefix.ROUTING_RULE);
  }

  /**
   * Generate a routing rule condition ID
   *
   * @return the generated routing rule condition ID
   */
  public static String generateRoutingRuleConditionId() {
    return generateId(IdPrefix.ROUTING_RULE_CONDITION);
  }

  /**
   * Generate a routing rule PSP ID
   *
   * @return the generated routing rule PSP ID
   */
  public static String generateRoutingRulePspId() {
    return generateId(IdPrefix.ROUTING_RULE_PSP);
  }

  /**
   * Generate a permission ID
   *
   * @return the generated permission ID
   */
  public static String generatePermissionId() {
    return generateId(IdPrefix.PERMISSION);
  }

  /**
   * Generate a random ID without prefix
   *
   * @return the generated random ID
   */
  private static String generateRandomId() {
    StringBuilder sb = new StringBuilder(26); // 26 characters for the random part
    for (int i = 0; i < 26; i++) {
      int randomIndex = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
      sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
    }
    return sb.toString();
  }

  /**
   * Validate if an ID has the correct prefix
   *
   * @param id the ID to validate
   * @param expectedPrefix the expected prefix
   * @return true if the ID has the correct prefix
   */
  public static boolean hasPrefix(String id, String expectedPrefix) {
    if (id == null || expectedPrefix == null) {
      return false;
    }

    if (expectedPrefix.isEmpty()) {
      // For tokens, no prefix is expected
      return !id.contains("_");
    }

    return id.startsWith(expectedPrefix + "_");
  }

  /**
   * Extract the prefix from an ID
   *
   * @param id the ID to extract prefix from
   * @return the prefix, or empty string if no prefix
   */
  public static String extractPrefix(String id) {
    if (id == null || !id.contains("_")) {
      return "";
    }
    return id.substring(0, id.indexOf("_"));
  }
}
