package com.game.chess.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chessPlatformOpenAPI() {
        final String securitySchemeName = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIiLCJpYXQiOjE3OTM3OTIyOTcsImV4cCI6MTc5Mzg3ODY5N30.44rQPgYDVtLmAq7DEsIV3vQ2o4cheUCeshV0H6wB9KDfOA381VpCCf3l21jDbTxKvGmnr4KemL8SmgLiJfCNEg";
        return new OpenAPI()
                .info(new Info()
                        .title("Chess Platform API")
                        .description("Backend REST API documentation for wallets, escrow, and matchmaking.")
                        .version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}