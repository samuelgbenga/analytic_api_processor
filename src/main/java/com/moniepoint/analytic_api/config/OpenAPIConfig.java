package com.moniepoint.analytic_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Analitic API Processor",
        version = "1.0.0",
        description = "RESTFUL ANALYTIC API FORM MONIEPOINT",
        contact = @Contact(
            name = "API Support",
            email = "support@bookmanagement.com"
        )
    ),
    servers = {
        @Server(
            description = "Local Development",
            url = "http://localhost:8080"
        )
    }
)
public class OpenAPIConfig {
}