package nexxus.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "security.auth0")
@Getter
@Setter
public class Auth0Properties {
  private String domain;
  private String clientId;
  private String clientSecret;
  private String audience;
  private String issuerUri;
  private String jwksUri;
  private String callbackUrl;
  private String frontendUrl;
}
