package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.FranchiseServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {

    // Definición manual del Logger (al no usar Lombok @Slf4j)
    private static final Logger log = LoggerFactory.getLogger(FranchiseController.class);

    private final FranchiseServicePort franchiseService;

    public FranchiseController(FranchiseServicePort franchiseService) {
        this.franchiseService = franchiseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> createFranchise(@RequestBody Franchise franchise) {
        return franchiseService.create(franchise)
            .doFirst(() -> log.info(">> Iniciando creación de franquicia: {}", franchise.getName()))
            .doOnSuccess(f -> log.info("<< Franquicia creada exitosamente con ID: {}", f.getId()))
            .doOnError(e -> log.error("!! Error al crear franquicia: {}", e.getMessage()));
    }

    @PutMapping("/{id}/name")
    public Mono<Franchise> updateName(@PathVariable Long id, @RequestParam String name) {
        return franchiseService.updateName(id, name)
            .doFirst(() -> log.info(">> Solicitud de cambio de nombre para franquicia ID: {} a '{}'", id, name))
            .doOnNext(f -> log.info("<< Nombre de franquicia ID: {} actualizado correctamente", id))
            .doOnError(e -> log.error("!! Error al actualizar nombre de franquicia ID {}: {}", id, e.getMessage()));
    }

    @GetMapping("/{id}/top-products")
    public Flux<Product> getTopProducts(@PathVariable Long id) {
        return franchiseService.getTopProducts(id)
            .doFirst(() -> log.info(">> Consultando productos con mayor stock para la franquicia ID: {}", id))
            // Usamos doOnComplete para Flux porque doOnSuccess solo se dispara al terminar todo el flujo
            .doOnComplete(() -> log.info("<< Consulta de top productos para franquicia {} finalizada", id))
            .doOnError(e -> log.error("!! Error al obtener top productos de la franquicia {}: {}", id, e.getMessage()));
    }

    @PostMapping("/{id}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Branch> addBranch(@PathVariable Long id, @RequestBody Branch branch) {
        return franchiseService.addBranch(id, branch)
            .doFirst(() -> log.info(">> Agregando sucursal '{}' a la franquicia ID: {}", branch.getName(), id))
            .doOnSuccess(b -> log.info("<< Sucursal '{}' agregada con éxito (ID: {})", b.getName(), b.getId()))
            .doOnError(e -> log.error("!! Error al agregar sucursal a la franquicia {}: {}", id, e.getMessage()));
    }
}