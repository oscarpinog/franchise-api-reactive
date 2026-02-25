package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.BranchServicePort;
import com.franchise.domain.ports.out.BranchOutputPort;
import com.franchise.domain.ports.out.ProductOutputPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BranchServiceImpl implements BranchServicePort {

    private static final Logger log = LoggerFactory.getLogger(BranchServiceImpl.class);

    private final BranchOutputPort branchRepository;
    private final ProductOutputPort productRepository;

    public BranchServiceImpl(BranchOutputPort branchRepository, ProductOutputPort productRepository) {
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Branch> addBranch(Long franchiseId, Branch branch) {
        return Mono.just(branch)
                .doFirst(() -> log.info("Agregando nueva sucursal '{}' a la franquicia ID: {}", branch.getName(), franchiseId))
                .flatMap(b -> {
                    b.setFranchiseId(franchiseId);
                    return branchRepository.save(b);
                })
                .doOnSuccess(savedBranch -> log.info("Sucursal guardada exitosamente con ID: {}", savedBranch.getId()));
    }

    @Override
    public Mono<Branch> updateName(Long id, String name) {
        return branchRepository.findById(id)
                .doFirst(() -> log.info("Buscando sucursal ID: {} para actualizar nombre a '{}'", id, name))
                .flatMap(branch -> {
                    branch.rename(name);
                    return branchRepository.save(branch);
                })
                // Si findById no devuelve nada, lanzamos error para que el GlobalExceptionHandler lo capture
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró la sucursal con ID: " + id)))
                .doOnSuccess(updated -> log.info("Nombre de sucursal actualizado con éxito"));
    }

    @Override
    public Mono<Product> addProduct(Long branchId, Product product) {
        return branchRepository.findById(branchId)
                .doFirst(() -> log.info("Validando existencia de sucursal ID: {} para añadir producto '{}'", branchId, product.getName()))
                // Verificamos primero si la sucursal existe antes de guardar el producto
                .flatMap(branch -> {
                    product.setBranchId(branchId);
                    return productRepository.save(product);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se puede agregar el producto. La sucursal con ID " + branchId + " no existe.")))
                .doOnSuccess(p -> log.info("Producto '{}' vinculado correctamente a la sucursal {}", p.getName(), branchId))
                .doOnError(e -> log.error("Error al intentar agregar producto: {}", e.getMessage()));
    }
   
//    @Override
//    public Flux<Branch> findByFranchiseId(Long franchiseId) {
//        return branchRepository.findByFranchiseId(franchiseId)
//                .doFirst(() -> log.info("Buscando sucursales para franquicia ID: {}", franchiseId))
//                .switchIfEmpty(Flux.error(
//                        new IllegalArgumentException("No existen sucursales para la franquicia con ID: " + franchiseId)
//                ))
//                .doOnComplete(() -> log.info("Consulta de sucursales finalizada para franquicia ID: {}", franchiseId))
//                .doOnError(e -> log.error("Error consultando sucursales: {}", e.getMessage()));
//    }
}