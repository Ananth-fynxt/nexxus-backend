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
    if (properties == null) {
      throw new IllegalStateException("Auth0 properties are null - check configuration");
    }
    if (properties.getDomain() == null || properties.getDomain().isEmpty()) {
      throw new IllegalStateException(
          "Auth0 domain is not configured - check AUTH0_DOMAIN environment variable");
    }
    if (properties.getClientId() == null || properties.getClientId().isEmpty()) {
      throw new IllegalStateException(
          "Auth0 client ID is not configured - check AUTH0_CLIENT_ID environment variable");
    }
    if (properties.getAudience() == null || properties.getAudience().isEmpty()) {
      throw new IllegalStateException(
          "Auth0 audience is not configured - check AUTH0_AUDIENCE environment variable");
    }

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
    if (properties == null) {
      throw new IllegalStateException("Auth0 properties are null - check configuration");
    }
    if (properties.getDomain() == null || properties.getDomain().isEmpty()) {
      throw new IllegalStateException(
          "Auth0 domain is not configured - check AUTH0_DOMAIN environment variable");
    }
    if (properties.getClientId() == null || properties.getClientId().isEmpty()) {
      throw new IllegalStateException(
          "Auth0 client ID is not configured - check AUTH0_CLIENT_ID environment variable");
    }

    String encodedReturnTo = urlEncode(returnTo);
    String base = "https://" + properties.getDomain() + "/v2/logout";
    return base + "?client_id=" + properties.getClientId() + "&returnTo=" + encodedReturnTo;
  }

  public boolean isConfigured() {
    return properties != null
        && properties.getDomain() != null
        && !properties.getDomain().isEmpty()
        && properties.getClientId() != null
        && !properties.getClientId().isEmpty()
        && properties.getAudience() != null
        && !properties.getAudience().isEmpty();
  }

  public String getDomain() {
    return properties != null ? properties.getDomain() : "NOT_CONFIGURED";
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
