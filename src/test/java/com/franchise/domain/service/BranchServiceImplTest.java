package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.out.BranchOutputPort;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchOutputPort branchRepository;

    @Mock
    private ProductOutputPort productRepository;

    @Spy // Usamos Spy para que ejecute la lógica real del fallback si es necesario
    private ResilienceFallback resilienceFallback;

    @InjectMocks
    private BranchServiceImpl branchService;

    private Branch sampleBranch;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleBranch = new Branch();
        sampleBranch.setId(1L);
        sampleBranch.setName("Sucursal Centro");

        sampleProduct = new Product();
        sampleProduct.setId(10L);
        sampleProduct.setName("Producto A");
    }

    @Test
    @DisplayName("Debe agregar una sucursal exitosamente")
    void addBranchSuccess() {
        // CAMBIO: Usar Mono.just() en lugar de Mono.of()
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(sampleBranch));

        StepVerifier.create(branchService.addBranch(1L, sampleBranch))
                .expectNextMatches(saved -> saved.getName().equals("Sucursal Centro"))
                .verifyComplete();

        verify(branchRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe actualizar el nombre de la sucursal")
    void updateNameSuccess() {
        when(branchRepository.findById(1L)).thenReturn(Mono.just(sampleBranch));
        when(branchRepository.save(any())).thenReturn(Mono.just(sampleBranch));

        StepVerifier.create(branchService.updateName(1L, "Nuevo Nombre"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe agregar un producto a la sucursal exitosamente")
    void addProductSuccess() {
        when(branchRepository.findById(1L)).thenReturn(Mono.just(sampleBranch));
        when(productRepository.save(any())).thenReturn(Mono.just(sampleProduct));

        StepVerifier.create(branchService.addProduct(1L, sampleProduct))
                .expectNextMatches(p -> p.getName().equals("Producto A"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar error 404 si la sucursal no existe al agregar producto")
    void addProductBranchNotFound() {
        when(branchRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(branchService.addProduct(1L, sampleProduct))
                .expectError() // El ValidationHelper lanzará la excepción de negocio
                .verify();
    }
}