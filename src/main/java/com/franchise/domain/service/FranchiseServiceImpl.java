package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.FranchiseServicePort;
import com.franchise.domain.ports.out.BranchOutputPort;
import com.franchise.domain.ports.out.FranchiseOutputPort;
import com.franchise.domain.ports.out.ProductOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FranchiseServiceImpl implements FranchiseServicePort {

    private static final Logger log = LoggerFactory.getLogger(FranchiseServiceImpl.class);

    private final FranchiseOutputPort franchiseRepository;
    private final BranchOutputPort branchRepository;
    private final ProductOutputPort productRepository;

    public FranchiseServiceImpl(FranchiseOutputPort franchiseRepository, 
                                BranchOutputPort branchRepository,
                                ProductOutputPort productRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        return Mono.just(franchise)
                .doFirst(() -> log.info("Registrando nueva franquicia: {}", franchise.getName()))
                .flatMap(franchiseRepository::save)
                .doOnSuccess(f -> log.info("Franquicia '{}' creada exitosamente con ID: {}", f.getName(), f.getId()));
    }

    @Override
    public Mono<Franchise> updateName(Long id, String name) {
        return franchiseRepository.findById(id)
                .doFirst(() -> log.info("Intentando actualizar nombre de franquicia ID: {} a '{}'", id, name))
                .flatMap(f -> {
                    f.setName(name);
                    return franchiseRepository.save(f);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Error: Franquicia no encontrada con ID: " + id)))
                .doOnSuccess(f -> log.info("Nombre de franquicia actualizado correctamente"));
    }

    @Override
    public Flux<Product> getTopProducts(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .doFirst(() -> log.info("Generando reporte de productos top para franquicia ID: {}", franchiseId))
                // Si la franquicia no existe, cortamos el flujo con error
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No existe la franquicia con ID: " + franchiseId)))
                .thenMany(branchRepository.findByFranchiseId(franchiseId))
                .flatMap(branch -> {
                    log.debug("Buscando producto con mayor stock en sucursal: {}", branch.getName());
                    return productRepository.findTopByBranchIdOrderByStockDesc(branch.getId());
                })
                .doOnComplete(() -> log.info("Reporte de top productos para franquicia {} finalizado con éxito", franchiseId));
    }

    @Override
    public Mono<Branch> addBranch(Long franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .doFirst(() -> log.info("Validando franquicia ID: {} para agregar sucursal '{}'", franchiseId, branch.getName()))
                // Aseguramos integridad: la franquicia debe existir
                .flatMap(f -> {
                    branch.setFranchiseId(franchiseId);
                    return branchRepository.save(branch);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se puede agregar sucursal. La franquicia " + franchiseId + " no existe.")))
                .doOnSuccess(b -> log.info("Sucursal '{}' vinculada correctamente a la franquicia {}", b.getName(), franchiseId));
    }
}