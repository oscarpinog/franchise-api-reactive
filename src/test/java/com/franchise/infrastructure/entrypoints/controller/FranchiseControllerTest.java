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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(FranchiseController.class)
public class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseServicePort franchiseService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe crear una franquicia exitosamente")
    void createFranchiseSuccess() {
        Franchise franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Franquicia Master");

        Mockito.when(franchiseService.create(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Franquicia Master");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar el nombre de la franquicia exitosamente")
    void updateNameSuccess() {
        Franchise franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Nuevo Nombre");

        Mockito.when(franchiseService.updateName(eq(1L), anyString()))
                .thenReturn(Mono.just(franchise));

        webTestClient.mutateWith(csrf())
                .put()
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe obtener el top de productos por franquicia")
    void getTopProductsSuccess() {
        Product p1 = new Product();
        p1.setName("Top Product");
        p1.setStock(50);

        Mockito.when(franchiseService.getTopProducts(anyLong()))
                .thenReturn(Flux.just(p1));

        webTestClient.get()
                .uri("/api/franchises/1/top-products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe agregar una sucursal a la franquicia exitosamente")
    void addBranchSuccess() {
        // Llenamos el objeto Branch con los campos que suelen ser obligatorios
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setName("Sucursal Central");
        branch.setFranchiseId(1L); // Campo obligatorio común en validaciones

        Mockito.when(franchiseService.addBranch(anyLong(), any(Branch.class)))
                .thenReturn(Mono.just(branch));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/franchises/1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(branch)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sucursal Central");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe fallar si el ID de franquicia es negativo")
    void franchiseIdNegativeFail() {
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/api/franchises/-5/top-products")
                .exchange()
                .expectStatus().isBadRequest();
    }
}