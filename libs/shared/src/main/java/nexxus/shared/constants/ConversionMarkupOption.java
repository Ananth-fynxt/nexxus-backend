package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConversionMarkupOption {
  FIXED_PER_UNIT("FIXED_PER_UNIT"),
  PERCENTAGE("PERCENTAGE");

  private final String value;

  ConversionMarkupOption(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
