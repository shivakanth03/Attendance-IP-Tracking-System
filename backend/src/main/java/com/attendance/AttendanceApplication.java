package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the Smart Attendance & Network Verification System.
 *
 * <p>This Spring Boot application provides:
 * <ul>
 *   <li>REST APIs for attendance management</li>
 *   <li>JWT-based authentication and authorization</li>
 *   <li>QR Code generation and validation</li>
 *   <li>Real-time WebSocket updates</li>
 *   <li>Java Socket Server for network monitoring</li>
 *   <li>Network IP validation for campus-only attendance</li>
 * </ul>
 *
 * @author Smart Attendance Team
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableCaching
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
public class AttendanceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AttendanceApplication.class);
        app.run(args);

        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║     Smart Attendance & Network Verification System           ║");
        log.info("║     Version: 1.0.0  |  Java 21  |  Spring Boot 3.x          ║");
        log.info("║     Swagger UI : http://localhost:8080/swagger-ui.html       ║");
        log.info("║     API Docs   : http://localhost:8080/api-docs              ║");
        log.info("║     Socket     : ws://localhost:9090                         ║");
        log.info("╚══════════════════════════════════════════════════════════════╝");
    }
}
