package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConversionFetchOption {
  REAL_TIME("REAL_TIME"),
  PREVIOUS_DAY_CLOSING("PREVIOUS_DAY_CLOSING");

  private final String value;

  ConversionFetchOption(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
