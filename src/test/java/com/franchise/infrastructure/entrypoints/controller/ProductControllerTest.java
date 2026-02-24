package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.ProductServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductServicePort productService;

    @Test
    @DisplayName("DELETE /api/products/{id} - Debe retornar 204 No Content")
    void deleteProduct_Success() {
        Mockito.when(productService.delete(100L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/products/100")
                .exchange()
                .expectStatus().isNoContent() // Verifica el código 204
                .expectBody().isEmpty();     // Verifica que no hay cuerpo
    }

    @Test
    @DisplayName("PATCH /api/products/{id}/stock - Debe retornar 200 y producto actualizado")
    void updateStock_Success() {
        Product product = new Product();
        product.setId(100L);
        product.setStock(50);

        Mockito.when(productService.updateStock(eq(100L), anyInt()))
               .thenReturn(Mono.just(product));

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/100/stock")
                        .queryParam("stock", 50)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(50);
    }

    @Test
    @DisplayName("PUT /api/products/{id}/name - Debe retornar 200 y nuevo nombre")
    void updateProductName_Success() {
        Product product = new Product();
        product.setId(100L);
        product.setName("Nuevo Nombre");

        Mockito.when(productService.updateName(eq(100L), anyString()))
               .thenReturn(Mono.just(product));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/100/name")
                        .queryParam("name", "Nuevo Nombre")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nuevo Nombre");
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Debe retornar 404 si no existe")
    void deleteProduct_NotFound() {
        Mockito.when(productService.delete(99L))
               .thenReturn(Mono.error(new IllegalArgumentException("No existe")));

        webTestClient.delete()
                .uri("/api/products/99")
                .exchange()
                .expectStatus().isNotFound(); // Asumiendo que corregimos el ExceptionHandler
    }
}