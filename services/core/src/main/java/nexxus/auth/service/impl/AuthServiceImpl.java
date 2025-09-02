package nexxus.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import nexxus.auth.dto.TokenResponse;
import nexxus.auth.service.AuthService;
import nexxus.shared.config.Auth0Properties;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.dto.ApiResponse;
import nexxus.shared.util.ReactiveResponseHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Implementation of AuthService for handling authentication operations with Auth0. Follows SOLID
 * principles with single responsibility for Auth0 integration.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final Auth0Properties auth0Properties;
  private final ReactiveResponseHandler responseHandler;

  @Override
  public ReactiveResponseHandler getResponseHandler() {
    return responseHandler;
  }

  /**
   * Creates a WebClient instance configured for Auth0 API communication.
   *
   * @return Configured WebClient instance
   */
  private WebClient createWebClient() {
    return WebClient.builder().baseUrl("https://" + auth0Properties.getDomain()).build();
  }

  /**
   * Exchanges authorization code for access token with Auth0 OAuth2 service. Validates input
   * parameters and handles external service errors appropriately.
   *
   * @param code The authorization code received from Auth0
   * @param redirectUri The redirect URI used in the authorization request
   * @param state Optional state parameter for CSRF protection
   * @return Mono containing the token response wrapped in standardized API response
   */
  @Override
  public Mono<ResponseEntity<ApiResponse<Object>>> exchangeCodeForToken(
      String code, String redirectUri, String state) {
    try {
      // Validate input parameters
      validateNotBlank(code, "Authorization code");
      validateNotBlank(redirectUri, "Redirect URI");

      WebClient webClient = createWebClient();

      return webClient
          .post()
          .uri("/oauth/token")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(
              BodyInserters.fromFormData("grant_type", "authorization_code")
                  .with("client_id", auth0Properties.getClientId())
                  .with("client_secret", auth0Properties.getClientSecret())
                  .with("code", code)
                  .with("redirect_uri", redirectUri))
          .retrieve()
          .bodyToMono(TokenResponse.class)
          .flatMap(tokenResponse -> successResponse(tokenResponse, "Token exchanged successfully"))
          .onErrorResume(
              WebClientResponseException.class,
              ex ->
                  customError(
                      ErrorCode.EXTERNAL_SERVICE_ERROR,
                      "Auth0 token exchange failed: " + ex.getResponseBodyAsString(),
                      HttpStatus.BAD_GATEWAY))
          .onErrorResume(
              Exception.class,
              ex ->
                  customError(
                      ErrorCode.EXTERNAL_SERVICE_ERROR,
                      "Unexpected error during token exchange: " + ex.getMessage(),
                      HttpStatus.INTERNAL_SERVER_ERROR));

    } catch (Exception e) {
      return customError(
          ErrorCode.VALIDATION_ERROR,
          "Invalid request parameters for token exchange",
          HttpStatus.BAD_REQUEST);
    }
  }
}
