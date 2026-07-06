package com.attendance.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI 3 configuration for API documentation.
 * Access at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_KEY = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.attendance.edu")
                    .description("Production Server")
            ))
            .addSecurityItem(new SecurityRequirement().addList(BEARER_KEY))
            .components(new Components()
                .addSecuritySchemes(BEARER_KEY, jwtSecurityScheme())
            );
    }

    private Info apiInfo() {
        return new Info()
            .title("Smart Attendance & Network Verification System API")
            .description("""
                Enterprise-grade REST API for QR-based attendance management with:
                - JWT Authentication (Access + Refresh Tokens)
                - Role-Based Access Control (Super Admin / Admin / Student)
                - QR Code Session Management
                - Real-time WebSocket Updates
                - Network IP Validation
                - Comprehensive Audit Logging
                """)
            .version("1.0.0")
            .contact(new Contact()
                .name("Smart Attendance Team")
                .email("support@attendance.edu")
                .url("https://attendance.edu"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .name(BEARER_KEY)
            .description("Enter JWT Bearer token (without 'Bearer' prefix)");
    }
}
