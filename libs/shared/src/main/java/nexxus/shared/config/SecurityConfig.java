package nexxus.shared.config;

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

/** Reactive Security configuration for the application secured by Auth0 (OIDC/JWT) */
@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(Auth0Properties.class)
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
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
                        "/auth/login",
                        "/auth/logout",
                        "/auth/callback")
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
}
