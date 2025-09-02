package nexxus.shared.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Value("${swagger.enabled}")
  private boolean swaggerEnabled;

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${api.swagger.server-url}")
  private String serverUrl;

  @Value("${security.auth0.domain}")
  private String auth0Domain;

  @Bean
  public OpenAPI customOpenAPI() {
    if (!swaggerEnabled) {
      return new OpenAPI()
          .info(
              new Info()
                  .title(applicationName)
                  .description("API documentation is currently disabled"));
    }

    return new OpenAPI()
        .info(createInfo())
        .servers(createServers())
        .components(createComponents())
        .addSecurityItem(createSecurityRequirement());
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/v1/**")
        .packagesToScan("nexxus")
        .build();
  }

  private Info createInfo() {
    return new Info().title(applicationName);
  }

  private List<Server> createServers() {
    return List.of(new Server().url(serverUrl));
  }

  private Components createComponents() {
    Components components = new Components().addSecuritySchemes("bearerAuth", createBearerAuth());
    if (StringUtils.hasText(auth0Domain)) {
      components.addSecuritySchemes("oauth2", createAuth0OAuth2());
    }
    return components;
  }

  private SecurityScheme createBearerAuth() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description(
            "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"");
  }

  private SecurityScheme createAuth0OAuth2() {
    String authUrl = "https://" + auth0Domain + "/authorize";
    String tokenUrl = "https://" + auth0Domain + "/oauth/token";

    OAuthFlow flow =
        new OAuthFlow()
            .authorizationUrl(authUrl)
            .tokenUrl(tokenUrl)
            .refreshUrl(tokenUrl)
            .scopes(
                new Scopes()
                    .addString("openid", "OpenID Connect")
                    .addString("profile", "User profile information")
                    .addString("email", "User email address"));

    return new SecurityScheme()
        .type(SecurityScheme.Type.OAUTH2)
        .flows(new OAuthFlows().authorizationCode(flow))
        .description(
            "OAuth2 flow for Auth0 authentication. After login, JWT token will be automatically attached to all API requests.");
  }

  private SecurityRequirement createSecurityRequirement() {
    SecurityRequirement requirement = new SecurityRequirement().addList("bearerAuth");
    if (StringUtils.hasText(auth0Domain)) {
      requirement.addList("oauth2", List.of("openid", "profile", "email"));
    }
    return requirement;
  }
}
