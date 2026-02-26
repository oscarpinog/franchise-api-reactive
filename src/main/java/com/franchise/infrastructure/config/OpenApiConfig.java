package com.franchise.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {


	 @Value("${app.aws.url:https://*.awsapprunner.com}") // Valor por defecto si no existe la variable
	 private String awsUrl;

	@Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                    .title("Franchise Management API - Oscar Rodriguez 3.0")
                    .version("3.0")
                    .description("Servicio reactivo para la gestión de franquicias, sucursales y productos."))
                // 1. Agregar servidores para evitar errores de protocolo (HTTP vs HTTPS)
                .addServersItem(new Server().url(awsUrl).description("Servidor Producción"))
                //.addServersItem(new Server().url("https://iz5f632zbj.us-east-2.awsapprunner.com").description("Servidor AWS"))
                .addServersItem(new Server().url("http://localhost:8080").description("Servidor Local"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                    .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
    
}