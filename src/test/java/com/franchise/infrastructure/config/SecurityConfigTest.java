package com.franchise.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;


    @Test
    @DisplayName("Debe denegar el acceso (401) a endpoints protegidos si no hay token")
    void protectedEndpointsAreDeniedWithoutToken() {
        webTestClient.get()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(roles = "USER") // Simulamos un usuario con rol insuficiente
    @DisplayName("Debe denegar acceso (403) si el usuario no tiene rol ADMIN")
    void accessDeniedForWrongRole() {
        webTestClient.get()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isForbidden();
    }



    @Test
    @DisplayName("Debe permitir peticiones OPTIONS por CORS sin autenticación")
    void optionsArePublic() {
        webTestClient.options()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isOk();
    }
}