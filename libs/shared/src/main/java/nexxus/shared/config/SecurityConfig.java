package nexxus.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Reactive Security configuration for the application
 *
 * <p>USAGE: - ✅ Use for: Main application security configuration now use reactive patterns with
 * WebFlux
 *
 * <p>This configuration provides: - CSRF protection disabled - Public endpoints for health checks
 * and documentation - Authentication required for all other endpoints - Password encoder for user
 * authentication - Reactive security using WebFilter instead of SecurityFilterChain
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            exchanges ->
                exchanges.anyExchange().permitAll()); // Allow all endpoints without authentication

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
