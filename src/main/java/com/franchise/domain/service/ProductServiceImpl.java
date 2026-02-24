package com.franchise.domain.service;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.ProductServicePort;
import com.franchise.domain.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductServicePort {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepositoryPort productRepository;

    public ProductServiceImpl(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Product> addProduct(Long branchId, Product product) {
        return Mono.just(product)
                .doFirst(() -> log.info("Registrando producto '{}' para la sucursal ID: {}", product.getName(), branchId))
                .flatMap(p -> {
                    p.setBranchId(branchId);
                    return productRepository.save(p);
                })
                .doOnSuccess(saved -> log.info("Producto guardado exitosamente con ID: {}", saved.getId()));
    }

    @Override
    public Mono<Product> updateStock(Long id, Integer stock) {
        return productRepository.findById(id)
                .doFirst(() -> log.info("Solicitud para actualizar stock de producto ID: {} a {}", id, stock))
                .flatMap(p -> {
                    p.setStock(stock);
                    return productRepository.save(p);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el producto con ID: " + id)))
                .doOnSuccess(p -> log.info("Stock actualizado para '{}'", p.getName()));
    }

    @Override
    public Mono<Product> updateName(Long id, String name) {
        return productRepository.findById(id)
                .doFirst(() -> log.info("Solicitud para cambiar nombre de producto ID: {} a '{}'", id, name))
                .flatMap(p -> {
                    p.setName(name);
                    return productRepository.save(p);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el producto con ID: " + id)))
                .doOnSuccess(p -> log.info("Nombre actualizado correctamente para ID: {}", id));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return productRepository.findById(id)
                // 1. Si findById no emite nada, lanzamos el error de inmediato
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se puede eliminar: Producto con ID " + id + " no existe.")))
                // 2. Si llegamos aquí, el producto existe. Ahora mandamos a borrar.
                .flatMap(product -> productRepository.deleteById(product.getId()))
                // 3. Agregamos logs de confirmación
                .doOnSuccess(v -> log.info("<< Producto ID: {} eliminado exitosamente del repositorio", id));
    }
}