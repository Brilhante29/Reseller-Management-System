package com.br.mobiauto.configs;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reseller Backend")
                        .description("This backend system is responsible for the management of dealerships, providing tools for user, dealership, and business opportunity management, ensuring a secure and efficient process for buying and selling vehicles.")
                        .version("1"))
                .schemaRequirement("jwt_auth", creaSecurityScheme());
    }

    @Bean
    public GroupedOpenApi customApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch(
                        "/api/dealerships/**",
                        "/api/users/**",
                        "/api/opportunities/**",
                        "/api/auth/**"
                )
                .build();
    }

    private SecurityScheme creaSecurityScheme() {
        return new SecurityScheme().name("jwt_auth").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");
    }
}
