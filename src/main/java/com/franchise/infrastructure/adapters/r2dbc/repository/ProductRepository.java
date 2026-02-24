package com.franchise.infrastructure.adapters.r2dbc.repository;

import com.franchise.infrastructure.adapters.r2dbc.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo de Spring Data para la entidad Product.
 * Utiliza R2DBC para operaciones no bloqueantes con MySQL.
 */
@Repository
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    /**
     * Busca todos los productos asociados a una sucursal.
     */
    Flux<ProductEntity> findByBranchId(Long branchId);

    /**
     * Requerimiento #6: Obtiene el producto con el stock más alto para una sucursal específica.
     * Spring Data deriva la consulta automáticamente por el nombre del método:
     * findTop (limita a 1) ByBranchId (filtro) OrderByStockDesc (ordenamiento).
     */
    Mono<ProductEntity> findTopByBranchIdOrderByStockDesc(Long branchId);
}