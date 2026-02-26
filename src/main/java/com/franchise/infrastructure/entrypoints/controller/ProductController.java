package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.ProductServicePort;
import com.franchise.domain.util.DomainConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductServicePort productService;

    public ProductController(ProductServicePort productService) {
        this.productService = productService;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_PRODUCT_ID_POSITIVE) Long id) {
        return productService.delete(id);
    }

    @PatchMapping("/{id}/stock")
    public Mono<Product> updateStock(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_PRODUCT_ID_POSITIVE) Long id, 
            @RequestParam @Min(value = 0, message = DomainConstants.VALIDATION_PRODUCT_STOCK_MIN) Integer stock) {
        return productService.updateStock(id, stock);
    }

    @PutMapping("/{id}/name")
    public Mono<Product> updateProductName(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_PRODUCT_ID_POSITIVE) Long id, 
            @RequestParam @NotBlank(message = DomainConstants.VALIDATION_PRODUCT_NAME_NOT_BLANK) String name) {
        return productService.updateName(id, name);
    }
}