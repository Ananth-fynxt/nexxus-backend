package nexxus.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

  @Value("${api.prefix}")
  private String apiPrefix;

  @Value("${api.cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${api.cors.allowed-methods}")
  private String allowedMethods;

  @Value("${api.cors.allowed-headers}")
  private String allowedHeaders;

  @Value("${api.cors.allow-credentials}")
  private boolean allowCredentials;

  @Value("${api.cors.max-age}")
  private long maxAge;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns(allowedOrigins.split(","))
        .allowedMethods(allowedMethods.split(","))
        .allowedHeaders(allowedHeaders.split(","))
        .allowCredentials(allowCredentials)
        .maxAge(maxAge);
  }

  @Override
  public void configurePathMatching(PathMatchConfigurer configurer) {
    // This will add the configured API prefix to all controller mappings
    configurer.addPathPrefix(apiPrefix, c -> true);
  }
}
