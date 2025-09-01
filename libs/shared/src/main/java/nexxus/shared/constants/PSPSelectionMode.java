package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PSPSelectionMode {
  PRIORITY("PRIORITY"),
  RANDOM("RANDOM"),
  WEIGHTED("WEIGHTED");

  private final String value;

  PSPSelectionMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
