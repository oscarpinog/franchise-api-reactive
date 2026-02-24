package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.BranchServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(BranchController.class)
class BranchControllerTest {

    @Autowired
    private WebTestClient webTestClient; // El cliente para disparar peticiones

    @MockBean
    private BranchServicePort branchService; // Simulamos el puerto de entrada

    @Test
    @DisplayName("POST /api/branches/{id}/products - Debe retornar 201 y el producto creado")
    void addProduct_Success() {
        Product mockProduct = new Product();
        mockProduct.setId(500L);
        mockProduct.setName("Producto Test");
        mockProduct.setStock(10);

        Mockito.when(branchService.addProduct(eq(1L), any(Product.class)))
               .thenReturn(Mono.just(mockProduct));

        webTestClient.post()
                .uri("/api/branches/1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockProduct) // Enviamos el cuerpo
                .exchange()
                .expectStatus().isCreated() // Verificamos el 201
                .expectBody()
                .jsonPath("$.id").isEqualTo(500)
                .jsonPath("$.name").isEqualTo("Producto Test");
    }

    @Test
    @DisplayName("PUT /api/branches/{id}/name - Debe retornar 200 al actualizar nombre")
    void updateBranchName_Success() {
        Branch updatedBranch = new Branch();
        updatedBranch.setId(1L);
        updatedBranch.setName("Nuevo Nombre");

        Mockito.when(branchService.updateName(1L, "Nuevo Nombre"))
               .thenReturn(Mono.just(updatedBranch));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/branches/1/name")
                        .queryParam("name", "Nuevo Nombre")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nuevo Nombre");
    }

    @Test
    @DisplayName("PUT /api/branches/{id}/name - Debe retornar 404 cuando la sucursal no existe")
    void updateBranchName_Error() {
        // GIVEN
        Mockito.when(branchService.updateName(anyLong(), anyString()))
               .thenReturn(Mono.error(new IllegalArgumentException("No se encontró la sucursal")));

        // WHEN & THEN
        webTestClient.put()
                .uri("/api/branches/99/name?name=Error")
                .exchange()
                .expectStatus().isNotFound(); // Cambiamos is5xxServerError por isNotFound (404)
                // O también podrías usar .expectStatus().isBadRequest() si devuelve 400
    }
}