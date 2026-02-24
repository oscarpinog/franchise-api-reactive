package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.FranchiseServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;

@WebFluxTest(FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseServicePort franchiseService;

    @Test
    @DisplayName("POST /api/franchises - Debe retornar 201 y la franquicia creada")
    void createFranchise_Success() {
        Franchise franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Franquicia Test");

        Mockito.when(franchiseService.create(any(Franchise.class)))
               .thenReturn(Mono.just(franchise));

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Franquicia Test");
    }

    @Test
    @DisplayName("GET /api/franchises/{id}/top-products - Debe retornar un Flux de productos")
    void getTopProducts_Success() {
        Product p1 = new Product();
        p1.setName("Producto A");
        p1.setStock(100);

        Mockito.when(franchiseService.getTopProducts(1L))
               .thenReturn(Flux.just(p1));

        webTestClient.get()
                .uri("/api/franchises/1/top-products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Producto A")
                .jsonPath("$[0].stock").isEqualTo(100);
    }

    @Test
    @DisplayName("PUT /api/franchises/{id}/name - Debe retornar 200 al actualizar")
    void updateName_Success() {
        Franchise updated = new Franchise();
        updated.setId(1L);
        updated.setName("Nuevo Nombre");

        Mockito.when(franchiseService.updateName(eq(1L), anyString()))
               .thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/franchises/1/name")
                        .queryParam("name", "Nuevo Nombre")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nuevo Nombre");
    }

    @Test
    @DisplayName("POST /api/franchises/{id}/branches - Debe retornar 201 al agregar sucursal")
    void addBranch_Success() {
        Branch branch = new Branch();
        branch.setId(10L);
        branch.setName("Sucursal Test");

        Mockito.when(franchiseService.addBranch(eq(1L), any(Branch.class)))
               .thenReturn(Mono.just(branch));

        webTestClient.post()
                .uri("/api/franchises/1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(branch)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sucursal Test");
    }

    @Test
    @DisplayName("GET /api/franchises/{id}/top-products - Debe retornar 404 si la franquicia no existe")
    void getTopProducts_NotFound() {
        Mockito.when(franchiseService.getTopProducts(99L))
               .thenReturn(Flux.error(new IllegalArgumentException("No existe la franquicia")));

        webTestClient.get()
                .uri("/api/franchises/99/top-products")
                .exchange()
                // Si tienes configurado un ExceptionHandler para 404:
                .expectStatus().isNotFound(); 
    }
}