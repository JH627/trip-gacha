package com.gacha.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Json Web Token(JWT)");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Json Web Token(JWT)", securityScheme))
                .security(Collections.singletonList(securityRequirement))
                .info(new Info().title("TripGacha API")
                        .description("TripGacha 명세서")
                        .version("v1.0.0"));
    }
	
}

