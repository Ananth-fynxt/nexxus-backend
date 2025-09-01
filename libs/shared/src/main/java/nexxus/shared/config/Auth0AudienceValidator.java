package nexxus.shared.config;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** Validates that the JWT contains the expected audience */
public class Auth0AudienceValidator implements OAuth2TokenValidator<Jwt> {
  private final String expectedAudience;

  public Auth0AudienceValidator(String expectedAudience) {
    this.expectedAudience = expectedAudience;
  }

  @Override
  public OAuth2TokenValidatorResult validate(Jwt token) {
    List<String> audiences = token.getAudience();
    if (audiences != null && audiences.contains(expectedAudience)) {
      return OAuth2TokenValidatorResult.success();
    }

    OAuth2Error error =
        new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "The required audience is missing", null);
    return OAuth2TokenValidatorResult.failure(error);
  }
}
