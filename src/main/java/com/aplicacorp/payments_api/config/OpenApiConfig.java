package com.aplicacorp.payments_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentsApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payments API")
                        .version("v1")
                        .description("""
                    REST API for creating and retrieving payments.

                    Payments are persisted and then transformed to XML
                    before being sent to the core payment system.
                    """)
                        .contact(new Contact()
                                .name("AplicaCorp")
                        )
                        .license(new License()
                                .name("Internal Technical Test")))
                .components(new Components()
                        .addSecuritySchemes(
                                "basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("HTTP Basic Authentication")
                        )
                );
    }
}
