package com.franchise.domain.service;

import com.franchise.domain.model.Product;
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
class ProductServiceImplTest {

    @Mock
    private ProductOutputPort productRepository;

    @Spy
    private ResilienceFallback resilienceFallback;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(100L);
        product.setName("Producto Base");
        product.setStock(10);
        product.setBranchId(1L);
    }

    @Test
    @DisplayName("Debe agregar un producto correctamente")
    void addProductSuccess() {
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productService.addProduct(1L, product))
                .expectNextMatches(p -> p.getName().equals("Producto Base"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar el stock de un producto")
    void updateStockSuccess() {
        when(productRepository.findById(100L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productService.updateStock(100L, 50))
                .expectNextMatches(p -> p.getStock() == 50)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar el nombre de un producto")
    void updateNameSuccess() {
        when(productRepository.findById(100L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productService.updateName(100L, "Nuevo Nombre"))
                .expectNextMatches(p -> p.getName().equals("Nuevo Nombre"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe eliminar un producto correctamente")
    void deleteProductSuccess() {
        when(productRepository.findById(100L)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.delete(100L))
                .verifyComplete();

        verify(productRepository).deleteById(100L);
    }

}