package com.franchise.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CorsConfigTest {

    @Autowired
    private CorsWebFilter corsWebFilter;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Debe existir el Bean de CorsWebFilter en el contexto")
    void corsWebFilterBeanExists() {
        assertThat(corsWebFilter).isNotNull();
    }

    @Test
    @DisplayName("Debe permitir peticiones Pre-flight (OPTIONS) desde el origen configurado")
    void shouldAllowCorsPreFlight() {
        webTestClient.options()
                .uri("/api/franchises")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type, Authorization")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:4200")
                .expectHeader().valueEquals("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH,OPTIONS");
    }

    @Test
    @DisplayName("Debe rechazar orígenes no permitidos")
    void shouldRejectForbiddenOrigin() {
        webTestClient.options()
                .uri("/api/franchises")
                .header("Origin", "http://malicious-site.com")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectHeader().doesNotExist("Access-Control-Allow-Origin");
    }
}