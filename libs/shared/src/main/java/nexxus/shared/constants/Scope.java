package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Scope {
  SYSTEM("SYSTEM"),
  BRAND("BRAND");

  private final String value;

  Scope(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
