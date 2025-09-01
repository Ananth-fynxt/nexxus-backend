package nexxus.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
  private String access_token;
  private Integer expires_in;
  private String token_type;
  private String scope;
  private String id_token;
}
