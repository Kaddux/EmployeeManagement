package com.pm.employeeservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Employee Management API")
                            .version("1.0.0")
                            .description("API documentation for the Employee Management system"))
                    .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                    .components(new Components()
                            .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .description("Provide the JWT token. Obtain it by logging in via POST /login")));
        }
}
