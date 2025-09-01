package nexxus.shared.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import nexxus.shared.config.Auth0Properties;

@Service
public class AuthUrlService {

  private final Auth0Properties properties;

  public AuthUrlService(Auth0Properties properties) {
    this.properties = properties;
  }

  public String buildLoginUrl(String redirectUri, String state) {
    String encodedRedirect = urlEncode(redirectUri);
    String encodedAudience = urlEncode(properties.getAudience());
    String base = "https://" + properties.getDomain() + "/authorize";
    String scope = urlEncode("openid profile email");
    String stateParam = state == null ? "" : "&state=" + urlEncode(state);
    return base
        + "?response_type=code"
        + "&client_id="
        + properties.getClientId()
        + "&redirect_uri="
        + encodedRedirect
        + "&scope="
        + scope
        + "&audience="
        + encodedAudience
        + stateParam;
  }

  public String buildLogoutUrl(String returnTo) {
    String encodedReturnTo = urlEncode(returnTo);
    String base = "https://" + properties.getDomain() + "/v2/logout";
    return base + "?client_id=" + properties.getClientId() + "&returnTo=" + encodedReturnTo;
  }

  private String urlEncode(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      // Should never happen for UTF-8
      throw new IllegalArgumentException("Unable to URL-encode value", e);
    }
  }
}
