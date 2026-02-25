package com.franchise.infrastructure.adapters.r2dbc;

import com.franchise.domain.model.Branch;
import com.franchise.domain.ports.out.BranchOutputPort;
import com.franchise.infrastructure.adapters.r2dbc.mapper.BranchEntityMapper;
import com.franchise.infrastructure.adapters.r2dbc.repository.BranchRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BranchAdapter implements BranchOutputPort {

    private static final Logger log = LoggerFactory.getLogger(BranchAdapter.class);
    private final BranchRepository repository;

    public BranchAdapter(BranchRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return repository.save(BranchEntityMapper.toEntity(branch))
                .map(BranchEntityMapper::toDomain)
                .doFirst(() -> log.debug("Persistiendo entidad Branch en DB"))
                .doOnSuccess(b -> log.debug("Branch guardada con ID: {}", b.getId()))
                .doOnError(e -> log.error("Error al guardar Branch: {}", e.getMessage()));
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return repository.findById(id)
                .map(BranchEntityMapper::toDomain)
                .doFirst(() -> log.debug("Buscando registro Branch ID: {}", id))
                .doOnNext(b -> log.debug("Registro Branch encontrado para ID: {}", id));
    }

    @Override
    public Flux<Branch> findByFranchiseId(Long franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(BranchEntityMapper::toDomain)
                .doFirst(() -> log.debug("Consultando sucursales para franquicia ID: {}", franchiseId))
                .doOnComplete(() -> log.debug("Consulta de sucursales finalizada para franquicia: {}", franchiseId));
    }   

}