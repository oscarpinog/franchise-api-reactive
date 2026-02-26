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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(BranchController.class)
public class BranchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BranchServicePort branchService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe agregar un producto exitosamente a una sucursal")
    void addProductSuccess() {
        // 1. Crear el objeto con el campo que faltaba
        Product productRequest = new Product();
        productRequest.setId(1L);
        productRequest.setName("Producto Test");
        productRequest.setStock(10);
        productRequest.setBranchId(1L); // <--- AGREGA ESTA LÍNEA ✅

        // 2. Mockear el servicio (usamos any() para evitar conflictos de coincidencia exacta)
        Mockito.when(branchService.addProduct(anyLong(), any()))
                .thenReturn(Mono.just(productRequest));

        // 3. Ejecutar la petición
        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/branches/1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isCreated() // Ahora sí será 201
                .expectBody()
                .jsonPath("$.name").isEqualTo("Producto Test")
                .jsonPath("$.branchId").isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe fallar al agregar producto con ID de sucursal negativo")
    void addProductValidationFail() {
        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/branches/-1/products") // ID negativo
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Product())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar el nombre de la sucursal exitosamente")
    void updateBranchNameSuccess() {
        // 1. Preparar datos
        String nuevoNombre = "Sucursal Norte";
        Branch branchResponse = new Branch();
        branchResponse.setId(1L);
        branchResponse.setName(nuevoNombre);

        // 2. Mockear servicio - IMPORTANTE: que coincidan los argumentos
        Mockito.when(branchService.updateName(eq(1L), eq(nuevoNombre)))
                .thenReturn(Mono.just(branchResponse));

        // 3. Ejecutar
        webTestClient.mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/branches/1/name")
                        .queryParam("name", nuevoNombre) // <--- Asegúrate que no sea vacío
                        .build())
                .exchange()
                .expectStatus().isOk() // Ahora debería dar 200
                .expectBody()
                .jsonPath("$.name").isEqualTo(nuevoNombre);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe fallar al actualizar nombre si el parámetro 'name' está en blanco")
    void updateBranchNameBlankFail() {
        webTestClient.mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/branches/1/name")
                        .queryParam("name", "") // Nombre vacío
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}