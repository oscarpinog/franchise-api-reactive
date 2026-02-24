package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.out.BranchRepositoryPort;
import com.franchise.domain.ports.out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchRepositoryPort branchRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private BranchServiceImpl branchService;

    private Branch sampleBranch;

    @BeforeEach
    void setUp() {
        sampleBranch = new Branch();
        sampleBranch.setId(10L);
        sampleBranch.setName("Sucursal Central");
        sampleBranch.setFranchiseId(1L);
    }

    @Test
    @DisplayName("Debe agregar una sucursal vinculándola a una franquicia")
    void addBranch_Success() {
        when(branchRepository.save(any(Branch.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Branch> result = branchService.addBranch(1L, sampleBranch);

        StepVerifier.create(result)
                .expectNextMatches(b -> b.getFranchiseId().equals(1L) && b.getName().equals("Sucursal Central"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar el nombre de una sucursal existente")
    void updateName_Success() {
        String newName = "Sucursal Norte";
        when(branchRepository.findById(10L)).thenReturn(Mono.just(sampleBranch));
        when(branchRepository.save(any(Branch.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Branch> result = branchService.updateName(10L, newName);

        StepVerifier.create(result)
                .expectNextMatches(b -> b.getName().equals(newName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe fallar al actualizar nombre si la sucursal no existe")
    void updateName_NotFound() {
        when(branchRepository.findById(10L)).thenReturn(Mono.empty());

        Mono<Branch> result = branchService.updateName(10L, "Nuevo Nombre");

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof IllegalArgumentException && 
                                    t.getMessage().contains("No se encontró la sucursal"))
                .verify();
    }

    @Test
    @DisplayName("Debe agregar un producto si la sucursal existe")
    void addProduct_Success() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(10);

        when(branchRepository.findById(10L)).thenReturn(Mono.just(sampleBranch));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(100L);
            return Mono.just(p);
        });

        Mono<Product> result = branchService.addProduct(10L, product);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getBranchId().equals(10L) && p.getName().equals("Producto A"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe fallar al agregar producto si la sucursal no existe")
    void addProduct_BranchNotFound() {
        when(branchRepository.findById(10L)).thenReturn(Mono.empty());

        Mono<Product> result = branchService.addProduct(10L, new Product());

        StepVerifier.create(result)
                .expectErrorMatches(t -> t.getMessage().contains("La sucursal con ID 10 no existe"))
                .verify();
        
        // Verificamos que nunca se intentó guardar el producto
        verify(productRepository, never()).save(any(Product.class));
    }
}