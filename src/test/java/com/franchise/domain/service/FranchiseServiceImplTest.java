package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.out.BranchOutputPort;
import com.franchise.domain.ports.out.FranchiseOutputPort;
import com.franchise.domain.ports.out.ProductOutputPort;
import com.franchise.domain.util.ResilienceFallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

    @Mock
    private FranchiseOutputPort franchiseRepository;
    @Mock
    private BranchOutputPort branchRepository;
    @Mock
    private ProductOutputPort productRepository;

    @Spy
    private ResilienceFallback resilienceFallback;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    private Franchise franchise;
    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Mega Corp");

        branch = new Branch();
        branch.setId(10L);
        branch.setName("Sucursal Norte");

        product = new Product();
        product.setId(100L);
        product.setName("Laptop Pro");
        product.setStock(50);
    }

    @Test
    @DisplayName("Debe crear una franquicia exitosamente")
    void createSuccess() {
        when(franchiseRepository.save(any())).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseService.create(franchise))
                .expectNextMatches(f -> f.getName().equals("Mega Corp"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe obtener los productos con más stock por sucursal")
    void getTopProductsSuccess() {
        // GIVEN: Una franquicia, una sucursal vinculada y un producto top
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByFranchiseId(1L)).thenReturn(Flux.just(branch));
        when(productRepository.findTopByBranchIdOrderByStockDesc(10L)).thenReturn(Mono.just(product));

        // WHEN & THEN
        StepVerifier.create(franchiseService.getTopProducts(1L))
                .expectNextMatches(p -> p.getName().equals("Laptop Pro") && p.getStock() == 50)
                .verifyComplete();

        verify(productRepository).findTopByBranchIdOrderByStockDesc(10L);
    }

    @Test
    @DisplayName("Debe retornar Flux vacío (fallback) cuando falla el reporte de top productos")
    void getTopProductsFallback() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        // Simulamos un error al buscar sucursales
        when(branchRepository.findByFranchiseId(1L)).thenReturn(Flux.error(new RuntimeException("Error DB")));

        StepVerifier.create(franchiseService.getTopProducts(1L))
                .verifyComplete(); // El fallback handleFluxError devuelve Flux.empty()

        verify(resilienceFallback).handleFluxError(any());
    }

    @Test
    @DisplayName("Debe agregar una sucursal a la franquicia")
    void addBranchToFranchiseSuccess() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchRepository.save(any())).thenReturn(Mono.just(branch));

        StepVerifier.create(franchiseService.addBranch(1L, branch))
                .expectNextMatches(b -> b.getFranchiseId().equals(1L))
                .verifyComplete();
    }

    
    @Test
    @DisplayName("Debe actualizar el nombre de la franquicia exitosamente")
    void updateNameSuccess() {
        // GIVEN
        String newName = "Franquicia Actualizada";
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        // WHEN
        StepVerifier.create(franchiseService.updateName(1L, newName))
                // THEN
                .expectNextMatches(f -> f.getName().equals(newName))
                .verifyComplete();

        verify(franchiseRepository).findById(1L);
        verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Debe lanzar error 404 si la franquicia no existe al intentar actualizar")
    void updateNameNotFound() {
        // GIVEN
        when(franchiseRepository.findById(1L)).thenReturn(Mono.empty());

        // WHEN
        StepVerifier.create(franchiseService.updateName(1L, "Nombre"))
                // THEN: Aquí no entra el fallback de Resilience4j porque es un error de validación de negocio
                .expectError() 
                .verify();
    }
}