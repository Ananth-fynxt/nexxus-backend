package nexxus.shared.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
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
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/** Reactive Security configuration for the application secured by Auth0 (OIDC/JWT) */
@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(Auth0Properties.class)
public class SecurityConfig {

  private final Auth0Properties auth0Properties;
  private final String apiPrefix;

  public SecurityConfig(Auth0Properties auth0Properties, @Value("${api.prefix}") String apiPrefix) {
    this.auth0Properties = auth0Properties;
    this.apiPrefix = apiPrefix;
  }

  private String[] getPublicPaths() {
    return combinePaths(getStaticPublicPaths(), getApiPublicPaths());
  }

  private String[] getStaticPublicPaths() {
    return new String[] {
      "/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**"
    };
  }

  private String[] getApiPublicPaths() {
    return new String[] {
      buildApiPath("/auth/login"),
      buildApiPath("/auth/logout"),
      buildApiPath("/auth/callback"),
      buildApiPath("/auth/config")
    };
  }

  private String[] combinePaths(String[]... pathArrays) {
    return java.util.Arrays.stream(pathArrays)
        .flatMap(java.util.Arrays::stream)
        .toArray(String[]::new);
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http,
      ReactiveJwtDecoder jwtDecoder,
      CorsConfigurationSource corsConfigurationSource) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeExchange(
            exchanges ->
                exchanges.pathMatchers(getPublicPaths()).permitAll().anyExchange().authenticated())
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
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList(auth0Properties.getFrontendUrl()));
    configuration.setAllowedMethods(Arrays.asList(getAllowedCorsMethods()));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private String[] getAllowedCorsMethods() {
    return new String[] {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"};
  }

  public String getApiPrefix() {
    return apiPrefix;
  }

  public String buildApiPath(String path) {
    if (path == null || path.isEmpty()) {
      return apiPrefix;
    }

    String normalizedPath = path.startsWith("/") ? path : "/" + path;
    return apiPrefix + normalizedPath;
  }

  @Bean
  public WebFluxConfigurer webFluxConfigurer() {
    return new WebFluxConfigurer() {
      @Override
      public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
            apiPrefix,
            c ->
                c.isAnnotationPresent(
                    org.springframework.web.bind.annotation.RequestMapping.class));
      }
    };
  }
}
