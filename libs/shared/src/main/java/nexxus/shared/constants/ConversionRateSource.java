package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConversionRateSource {
  FIXER_API("FIXER_API"),
  MANUAL("MANUAL"),
  CUSTOM_URL("CUSTOM_URL");

  private final String value;

  ConversionRateSource(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
