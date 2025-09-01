package nexxus.shared.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/** Reactive Security configuration for the application secured by Auth0 (OIDC/JWT) */
@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(Auth0Properties.class)
public class SecurityConfig {

  @Value("${frontend.origin}")
  private String frontendOrigin;

  @Value("${api.prefix}")
  private String apiPrefix;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http,
      ReactiveJwtDecoder jwtDecoder,
      CorsConfigurationSource corsConfigurationSource) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeExchange(
            exchanges ->
                exchanges
                    // Public endpoints
                    .pathMatchers(
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**",
                        apiPrefix + "/auth/login",
                        apiPrefix + "/auth/logout",
                        apiPrefix + "/auth/callback",
                        apiPrefix + "/auth/config")
                    .permitAll()
                    // All others must be authenticated
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder)));

    return http.build();
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder(Auth0Properties properties) {
    NimbusReactiveJwtDecoder decoder =
        NimbusReactiveJwtDecoder.withJwkSetUri(properties.getJwksUri()).build();

    OAuth2TokenValidator<Jwt> withIssuer =
        JwtValidators.createDefaultWithIssuer(properties.getIssuerUri());
    OAuth2TokenValidator<Jwt> withAudience = new Auth0AudienceValidator(properties.getAudience());
    OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();
    OAuth2TokenValidator<Jwt> validator =
        new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience, withTimestamp);

    decoder.setJwtValidator(validator);
    return decoder;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(
        Arrays.asList(frontendOrigin, "https://ca9a457eb330.ngrok-free.app"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
