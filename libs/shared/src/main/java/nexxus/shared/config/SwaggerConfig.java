package nexxus.shared.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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

  @Value("${spring.application.version}")
  private String applicationVersion;

  @Value("${spring.application.description}")
  private String applicationDescription;

  @Value("${api.swagger.server-url}")
  private String serverUrl;

  @Value("${api.swagger.server-description}")
  private String serverDescription;

  @Value("${api.swagger.contact.name}")
  private String contactName;

  @Value("${api.swagger.contact.email}")
  private String contactEmail;

  @Value("${api.swagger.contact.url}")
  private String contactUrl;

  @Value("${api.swagger.license.name}")
  private String licenseName;

  @Value("${api.swagger.license.url}")
  private String licenseUrl;

  @Value("${security.auth0.domain}")
  private String auth0Domain;

  @Bean
  public OpenAPI customOpenAPI() {
    if (!swaggerEnabled) {
      // Return a minimal OpenAPI spec when disabled
      return new OpenAPI()
          .info(
              new Info()
                  .title(applicationName)
                  .version(applicationVersion)
                  .description("API documentation is currently disabled"));
    }

    return new OpenAPI()
        .info(createInfo())
        .servers(createServers())
        .components(createComponents())
        .addSecurityItem(createSecurityRequirement());
  }

  private Info createInfo() {
    return new Info()
        .title(applicationName)
        .version(applicationVersion)
        .description(applicationDescription)
        .contact(createContact())
        .license(createLicense());
  }

  private Contact createContact() {
    return new Contact().name(contactName).email(contactEmail).url(contactUrl);
  }

  private License createLicense() {
    return new License().name(licenseName).url(licenseUrl);
  }

  private List<Server> createServers() {
    return List.of(new Server().url(serverUrl).description(serverDescription));
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
            .scopes(
                new Scopes()
                    .addString("openid", "OpenID Connect")
                    .addString("profile", "User profile information")
                    .addString("email", "User email address"));

    return new SecurityScheme()
        .type(SecurityScheme.Type.OAUTH2)
        .flows(new OAuthFlows().authorizationCode(flow))
        .description("OAuth2 flow for Auth0 authentication");
  }

  private SecurityRequirement createSecurityRequirement() {
    SecurityRequirement requirement = new SecurityRequirement().addList("bearerAuth");
    if (StringUtils.hasText(auth0Domain)) {
      requirement.addList("oauth2", List.of("openid", "profile", "email"));
    }
    return requirement;
  }
}
