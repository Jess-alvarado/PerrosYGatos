package com.auth.pyg_auth.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI pygAuthOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("PerrosYGatos - Auth API")
                                                .description("Microservicio de autenticación para la plataforma PerrosYGatos.\n"
                                                                +
                                                                "Incluye registro, login, y protección de endpoints con JWT.")
                                                .version("v1.0")
                                                .license(new License().name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("Repositorio del proyecto en GitHub")
                                                .url("https://github.com/Jess-alvarado/PerrosYGatos"));
        }
}
