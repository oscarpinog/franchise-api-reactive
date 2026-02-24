package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.ProductServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Definimos el logger
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductServicePort productService;

    public ProductController(ProductServicePort productService) {
        this.productService = productService;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productService.delete(id)
            .doFirst(() -> log.info(">> Solicitud DELETE para eliminar producto con ID: {}", id))
            .doOnSuccess(v -> log.info("<< Producto con ID: {} eliminado exitosamente", id))
            .doOnError(e -> log.error("!! Error al eliminar producto con ID: {}: {}", id, e.getMessage()));
    }

    @PatchMapping("/{id}/stock")
    public Mono<Product> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        return productService.updateStock(id, stock)
            .doFirst(() -> log.info(">> Solicitud PATCH para actualizar stock. ID: {}, Nuevo Stock: {}", id, stock))
            .doOnNext(p -> log.info("<< Stock actualizado para el producto: {} (ID: {})", p.getName(), p.getId()))
            .doOnError(e -> log.error("!! Error al actualizar stock para ID: {}: {}", id, e.getMessage()));
    }

    @PutMapping("/{id}/name")
    public Mono<Product> updateProductName(@PathVariable Long id, @RequestParam String name) {
        return productService.updateName(id, name)
            .doFirst(() -> log.info(">> Solicitud PUT para actualizar nombre. ID: {}, Nuevo Nombre: {}", id, name))
            .doOnNext(p -> log.info("<< Nombre actualizado exitosamente para ID: {}", id))
            .doOnError(e -> log.error("!! Error al actualizar nombre para ID: {}: {}", id, e.getMessage()));
    }
}