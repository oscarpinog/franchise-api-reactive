package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.ProductServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductServicePort productService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe eliminar un producto exitosamente (204 No Content)")
    void deleteProductSuccess() {
        Mockito.when(productService.delete(anyLong())).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .delete()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isNoContent(); // Verifica el 204
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar el stock exitosamente")
    void updateStockSuccess() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Producto");
        product.setStock(50);

        Mockito.when(productService.updateStock(eq(1L), eq(50)))
                .thenReturn(Mono.just(product));

        webTestClient.mutateWith(csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/1/stock")
                        .queryParam("stock", 50)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(50);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe fallar si el stock es negativo (@Min(0))")
    void updateStockNegativeFail() {
        webTestClient.mutateWith(csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/1/stock")
                        .queryParam("stock", -5) // Stock inválido
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar el nombre del producto exitosamente")
    void updateProductNameSuccess() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Nuevo Nombre");

        Mockito.when(productService.updateName(eq(1L), anyString()))
                .thenReturn(Mono.just(product));

        webTestClient.mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/1/name")
                        .queryParam("name", "Nuevo Nombre")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nuevo Nombre");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe fallar si el ID del producto no es positivo")
    void productIdInvalidFail() {
        webTestClient.mutateWith(csrf())
                .delete()
                .uri("/api/products/0") // ID inválido
                .exchange()
                .expectStatus().isBadRequest();
    }
}