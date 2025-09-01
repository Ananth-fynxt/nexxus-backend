package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FeeComponentType {
  FIXED("FIXED"),
  FIXED_PER_UNIT("FIXED_PER_UNIT"),
  PERCENTAGE("PERCENTAGE");

  private final String value;

  FeeComponentType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
