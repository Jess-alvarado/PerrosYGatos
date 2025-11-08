package com.owner.pyg_owner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Value("${server.port:8082}")
        private String serverPort;

        @Bean
        public OpenAPI pygOwnerOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("🐾 Pyg Owner API")
                                                .version("1.0.0")
                                                .description("API REST para la gestión de perfiles de dueños y mascotas en PerrosYGatos")
                                                .contact(new Contact()
                                                                .name("Jessica Alvarado")
                                                                .email("jess.alvarado@example.com")
                                                                .url("https://github.com/Jess-alvarado"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:" + serverPort)
                                                                .description("Servidor Local")))
                                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                                                .addList("bearerAuth"))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                                                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }
}