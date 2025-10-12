package com.auth.pyg_auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🐾 PerrosYGatos - Auth Service API")
                        .version("1.0.0")
                        .description("Servicio de autenticación para dueños de mascotas y profesionales (Spring Boot 3 + JWT)")
                        .contact(new Contact()
                                .name("Jessica Alvarado")
                                .email("contacto@perrosygatos.dev")
                        )
                )
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Entorno local")
                ));
    }
}
