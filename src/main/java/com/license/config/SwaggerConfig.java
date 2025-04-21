package com.license.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Name of the security scheme
        return new OpenAPI()
                .info(new Info()
                        .title("License Management API")
                        .description("API documentation for License Management with JWT authentication")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Apply the security scheme globally
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // HTTP authentication
                                .scheme("bearer") // Bearer token type
                                .bearerFormat("JWT"))); // Specify the token format
    }
}
