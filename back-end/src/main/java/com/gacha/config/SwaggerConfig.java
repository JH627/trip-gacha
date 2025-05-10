package com.gacha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
    public OpenAPI openAPI() {
		// JWT 토큰용
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Json Web Token(JWT)", securityScheme))
                .info(new Info().title("TripGacha API")
                        .description("TripGacha 명세서")
                        .version("v1.0.0"));
    }
	
}

