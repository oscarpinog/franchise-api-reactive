package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.out.BranchRepositoryPort;
import com.franchise.domain.ports.out.FranchiseRepositoryPort;
import com.franchise.domain.ports.out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private BranchRepositoryPort branchRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    private Franchise sampleFranchise;

    @BeforeEach
    void setUp() {
        sampleFranchise = new Franchise();
        sampleFranchise.setId(1L);
        sampleFranchise.setName("Franquicia Test");
    }

    @Test
    @DisplayName("Debe crear una franquicia exitosamente")
    void create_Success() {
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(sampleFranchise));

        Mono<Franchise> result = franchiseService.create(sampleFranchise);

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getName().equals("Franquicia Test") && f.getId() == 1L)
                .verifyComplete();
        
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Debe actualizar el nombre de una franquicia existente")
    void updateName_Success() {
        String newName = "Nuevo Nombre";
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(sampleFranchise));
        when(franchiseRepository.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Franchise> result = franchiseService.updateName(1L, newName);

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getName().equals(newName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar error al actualizar nombre si la franquicia no existe")
    void updateName_NotFound() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.empty());

        Mono<Franchise> result = franchiseService.updateName(1L, "Nombre");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Franquicia no encontrada"))
                .verify();
    }

    @Test
    @DisplayName("Debe obtener los productos top por sucursal")
    void getTopProducts_Success() {
        Long fId = 1L;
        Branch branch1 = new Branch(); branch1.setId(10L); branch1.setName("Sucursal A");
        Branch branch2 = new Branch(); branch2.setId(11L); branch2.setName("Sucursal B");
        
        Product p1 = new Product(); p1.setName("Laptop"); p1.setStock(50);
        Product p2 = new Product(); p2.setName("Mouse"); p2.setStock(100);

        when(franchiseRepository.findById(fId)).thenReturn(Mono.just(sampleFranchise));
        when(branchRepository.findByFranchiseId(fId)).thenReturn(Flux.just(branch1, branch2));
        when(productRepository.findTopByBranchIdOrderByStockDesc(10L)).thenReturn(Mono.just(p1));
        when(productRepository.findTopByBranchIdOrderByStockDesc(11L)).thenReturn(Mono.just(p2));

        Flux<Product> result = franchiseService.getTopProducts(fId);

        StepVerifier.create(result)
                .expectNext(p1)
                .expectNext(p2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar error en reporte si la franquicia no existe")
    void getTopProducts_FranchiseNotFound() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.empty());

        Flux<Product> result = franchiseService.getTopProducts(1L);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe agregar una sucursal correctamente")
    void addBranch_Success() {
        Branch newBranch = new Branch();
        newBranch.setName("Sucursal Norte");

        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(sampleFranchise));
        when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> {
            Branch b = invocation.getArgument(0);
            b.setId(50L);
            return Mono.just(b);
        });

        Mono<Branch> result = franchiseService.addBranch(1L, newBranch);

        StepVerifier.create(result)
                .expectNextMatches(b -> b.getFranchiseId().equals(1L) && b.getName().equals("Sucursal Norte"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe fallar al agregar sucursal si la franquicia no existe")
    void addBranch_FranchiseNotFound() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.empty());

        Mono<Branch> result = franchiseService.addBranch(1L, new Branch());

        StepVerifier.create(result)
                .expectErrorMatches(t -> t.getMessage().contains("La franquicia 1 no existe"))
                .verify();
    }
}