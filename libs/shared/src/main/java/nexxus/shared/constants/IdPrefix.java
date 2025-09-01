package nexxus.shared.constants;

/**
 * ID Prefix constants for different entity types Based on the TypeScript IdPrefix implementation
 */
public final class IdPrefix {

  // Brand related
  public static final String BRAND = "brn";

  // Environment related
  public static final String ENVIRONMENT = "env";
  public static final String ENVIRONMENT_SECRET = "sec";

  // Flow related
  public static final String FLOW_TYPE = "ftp";
  public static final String FLOW_ACTION = "fat";
  public static final String FLOW_TARGET = "ftg";
  public static final String FLOW_DEFINITION = "fld";
  public static final String FLOW = "flw";

  // PSP related
  public static final String PSP = "psp";

  // FX related
  public static final String FX_RATE_CONFIG = "fxc";
  public static final String FX_RATE_MARKUP = "fxm";

  // Conversion Rate related
  public static final String CONVERSION_RATE_CONFIG = "crc";
  public static final String CONVERSION_RATE_MARKUP = "crm";

  // Fee related
  public static final String FEE = "fee";
  public static final String FEE_COMPONENT = "fec";

  // Risk Rule related
  public static final String RISK_RULE = "rrl";
  public static final String RISK_RULE_CUSTOMER_CRITERIA = "rrc";

  // IP Rule related
  public static final String IP_RULE = "ipr";
  public static final String IP_RULE_ENTRY = "ire";

  // Maintenance Window related
  public static final String MAINTENANCE_WINDOW = "mwn";

  // Transaction related
  public static final String TRANSACTION = "txn";
  public static final String TRANSACTION_LOG = "tnl";

  // Webhook related
  public static final String WEBHOOK = "whk";
  public static final String WEBHOOK_LOG = "whl";

  // Routing Rule related
  public static final String ROUTING_RULE = "rtr";
  public static final String ROUTING_RULE_CONDITION = "rtc";
  public static final String ROUTING_RULE_PSP = "rtp";

  // Permission related
  public static final String PERMISSION = "prm";

  // Token (no prefix)
  public static final String TOKEN = "";

  private IdPrefix() {
    // Utility class, prevent instantiation
  }
}
