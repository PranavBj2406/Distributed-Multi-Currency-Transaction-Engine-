package com.bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Distributed Multi-Currency Transaction Engine")
                .version("1.0.0")
                .description("Production-style fintech REST API with live FX rate conversion, cross-border transfers and immutable audit logging.")
                .contact(new Contact()
                    .name("Pranav BJ")
                    .email("mail.pranavbj80@gmail.com")
                    .url("https://github.com/PranavBj2406")));
    }
}
