package com.franchise.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {


    @Test
    @DisplayName("Debe incluir la configuración de seguridad Bearer JWT")
    void customOpenAPISecurityTest() {
        // GIVEN
        OpenApiConfig config = new OpenApiConfig();
        String schemeName = "bearerAuth";

        // WHEN
        OpenAPI openAPI = config.customOpenAPI();

        // THEN
        // 1. Verificar componentes
        assertThat(openAPI.getComponents()).isNotNull();
        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get(schemeName);
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");

        // 2. CORRECCIÓN: Verificar el requisito de seguridad global
        assertThat(openAPI.getSecurity()).isNotEmpty();
        // SecurityRequirement extiende LinkedHashMap, pero en algunas versiones se accede así:
        boolean containsScheme = openAPI.getSecurity().stream()
                .anyMatch(requirement -> requirement.containsKey(schemeName));
        
        assertThat(containsScheme).isTrue();
    }
}