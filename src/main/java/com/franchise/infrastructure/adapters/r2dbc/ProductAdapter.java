package com.franchise.infrastructure.adapters.r2dbc;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.out.ProductRepositoryPort;
import com.franchise.infrastructure.adapters.r2dbc.mapper.ProductEntityMapper;
import com.franchise.infrastructure.adapters.r2dbc.repository.ProductRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ProductAdapter implements ProductRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(ProductAdapter.class);
    private final ProductRepository repository;

    public ProductAdapter(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(ProductEntityMapper.toEntity(product))
                .map(ProductEntityMapper::toDomain)
                .doFirst(() -> log.debug("Persistiendo producto en DB: {}", product.getName()))
                .doOnSuccess(p -> log.debug("Producto guardado con ID: {}", p.getId()))
                .doOnError(e -> log.error("Error al persistir producto: {}", e.getMessage()));
    }

    @Override
    public Mono<Product> findById(Long id) {
        return repository.findById(id)
                .map(ProductEntityMapper::toDomain)
                .doFirst(() -> log.debug("Buscando producto ID: {}", id))
                .doOnNext(p -> log.debug("Producto encontrado: {}", p.getName()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id)
                .doFirst(() -> log.debug("Ejecutando eliminación física en DB para producto ID: {}", id))
                .doOnSuccess(v -> log.debug("Eliminación completada para ID: {}", id))
                .doOnError(e -> log.error("Error al eliminar producto ID {}: {}", id, e.getMessage()));
    }

    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(Long branchId) {
        return repository.findTopByBranchIdOrderByStockDesc(branchId)
                .map(ProductEntityMapper::toDomain)
                .doFirst(() -> log.debug("Buscando producto con mayor stock para sucursal ID: {}", branchId))
                .doOnNext(p -> log.debug("Top producto encontrado en DB: {} (Stock: {})", p.getName(), p.getStock()));
    }
}