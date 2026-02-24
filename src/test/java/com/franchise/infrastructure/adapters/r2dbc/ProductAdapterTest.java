package com.franchise.infrastructure.adapters.r2dbc;

import com.franchise.domain.model.Product;
import com.franchise.infrastructure.adapters.r2dbc.entity.ProductEntity;
import com.franchise.infrastructure.adapters.r2dbc.repository.ProductRepository;
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
class ProductAdapterTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductAdapter productAdapter;

    private Product domainProduct;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        domainProduct = new Product();
        domainProduct.setName("Producto X");
        domainProduct.setStock(10);
        domainProduct.setBranchId(5L);

        productEntity = new ProductEntity();
        productEntity.setId(100L);
        productEntity.setName("Producto X");
        productEntity.setStock(10);
        productEntity.setBranchId(5L);
    }

    @Test
    @DisplayName("Debe guardar un producto y mapear la respuesta")
    void save_Success() {
        when(repository.save(any(ProductEntity.class))).thenReturn(Mono.just(productEntity));

        Mono<Product> result = productAdapter.save(domainProduct);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals(100L) && p.getName().equals("Producto X"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe eliminar un producto por ID")
    void deleteById_Success() {
        when(repository.deleteById(100L)).thenReturn(Mono.empty());

        Mono<Void> result = productAdapter.deleteById(100L);

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, times(1)).deleteById(100L);
    }

    @Test
    @DisplayName("Debe encontrar el producto con mayor stock de una sucursal")
    void findTopProduct_Success() {
        when(repository.findTopByBranchIdOrderByStockDesc(5L)).thenReturn(Mono.just(productEntity));

        Mono<Product> result = productAdapter.findTopByBranchIdOrderByStockDesc(5L);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getStock() == 10)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe devolver Mono vacío si no hay productos en la sucursal")
    void findTopProduct_Empty() {
        when(repository.findTopByBranchIdOrderByStockDesc(anyLong())).thenReturn(Mono.empty());

        Mono<Product> result = productAdapter.findTopByBranchIdOrderByStockDesc(99L);

        StepVerifier.create(result)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debe buscar un producto por ID y mapearlo a dominio")
    void findById_Success() {
        // GIVEN: El repositorio encuentra la entidad
        when(repository.findById(100L)).thenReturn(Mono.just(productEntity));

        // WHEN: Llamamos al adaptador
        Mono<Product> result = productAdapter.findById(100L);

        // THEN: Verificamos que el mapeo sea correcto y el flujo termine
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals(100L) && p.getName().equals("Producto X"))
                .verifyComplete();

        verify(repository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Debe devolver Mono vacío cuando el producto no existe en DB")
    void findById_Empty() {
        // GIVEN: El repositorio devuelve vacío
        when(repository.findById(999L)).thenReturn(Mono.empty());

        // WHEN: Llamamos al adaptador
        Mono<Product> result = productAdapter.findById(999L);

        // THEN: StepVerifier confirma que el flujo termina sin emitir nada
        StepVerifier.create(result)
                .verifyComplete(); 

        verify(repository).findById(999L);
    }
}