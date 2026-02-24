package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.BranchServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    // Definición del logger para BranchController
    private static final Logger log = LoggerFactory.getLogger(BranchController.class);

    private final BranchServicePort branchService;

    public BranchController(BranchServicePort branchService) {
        this.branchService = branchService;
    }

    @PostMapping("/{id}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> addProduct(@PathVariable Long id, @RequestBody Product product) {
        return branchService.addProduct(id, product)
            .doFirst(() -> log.info(">> Agregando producto '{}' a la sucursal ID: {}", product.getName(), id))
            .doOnSuccess(p -> log.info("<< Producto '{}' creado con éxito (ID: {}) en sucursal {}", p.getName(), p.getId(), id))
            .doOnError(e -> log.error("!! Error al agregar producto a la sucursal {}: {}", id, e.getMessage()));
    }

    @PutMapping("/{id}/name")
    public Mono<Branch> updateBranchName(@PathVariable Long id, @RequestParam String name) {
        return branchService.updateName(id, name)
            .doFirst(() -> log.info(">> Solicitud para renombrar sucursal ID: {} a '{}'", id, name))
            .doOnNext(b -> log.info("<< Sucursal ID: {} renombrada exitosamente a '{}'", id, b.getName()))
            .doOnError(e -> log.error("!! Error al actualizar nombre de sucursal ID {}: {}", id, e.getMessage()));
    }
}