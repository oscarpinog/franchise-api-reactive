package com.franchise.domain.service;

import com.franchise.domain.model.Product;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(100L);
        sampleProduct.setName("Producto Base");
        sampleProduct.setStock(10);
        sampleProduct.setBranchId(10L);
    }

    @Test
    @DisplayName("Debe registrar un producto vinculándolo a una sucursal")
    void addProduct_Success() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Product> result = productService.addProduct(10L, sampleProduct);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getBranchId().equals(10L) && p.getName().equals("Producto Base"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar el stock de un producto existente")
    void updateStock_Success() {
        Integer newStock = 50;
        when(productRepository.findById(100L)).thenReturn(Mono.just(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Product> result = productService.updateStock(100L, newStock);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getStock().equals(newStock))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar el nombre de un producto existente")
    void updateName_Success() {
        String newName = "Nombre Actualizado";
        when(productRepository.findById(100L)).thenReturn(Mono.just(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        Mono<Product> result = productService.updateName(100L, newName);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getName().equals(newName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe fallar al actualizar si el producto no existe")
    void update_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Mono.empty());

        Mono<Product> result = productService.updateStock(1L, 20);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof IllegalArgumentException && 
                                    t.getMessage().contains("No se encontró el producto"))
                .verify();
    }

    @Test
    @DisplayName("Debe eliminar un producto si existe")
    void delete_Success() {
        when(productRepository.findById(100L)).thenReturn(Mono.just(sampleProduct));
        when(productRepository.deleteById(100L)).thenReturn(Mono.empty()); // deleteById suele ser Mono<Void>

        Mono<Void> result = productService.delete(100L);

        StepVerifier.create(result)
                .verifyComplete(); // Verificamos que termine exitosamente sin emitir objetos

        verify(productRepository, times(1)).deleteById(100L);
    }

    @Test
    @DisplayName("Debe fallar al eliminar si el producto no existe")
    void delete_NotFound() {
        when(productRepository.findById(100L)).thenReturn(Mono.empty());

        Mono<Void> result = productService.delete(100L);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t.getMessage().contains("No se puede eliminar"))
                .verify();

        verify(productRepository, never()).deleteById(anyLong());
    }
}