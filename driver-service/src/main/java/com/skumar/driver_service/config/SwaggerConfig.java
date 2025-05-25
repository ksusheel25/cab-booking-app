package com.skumar.driver_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Driver Service API",
        version = "1.0",
        description = "API endpoints for driver management in cab booking system",
        contact = @Contact(
            name = "Sushil Kumar",
            email = "sushilkumar8081742575@gmail.com"
        )
    ),
    servers = {
        @Server(
            description = "Local Environment",
            url = "http://localhost:8082"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT token authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}