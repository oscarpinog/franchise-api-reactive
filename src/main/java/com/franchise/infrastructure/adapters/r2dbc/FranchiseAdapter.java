package com.franchise.infrastructure.adapters.r2dbc;

import com.franchise.domain.model.Franchise;
import com.franchise.domain.ports.out.FranchiseRepositoryPort;
import com.franchise.infrastructure.adapters.r2dbc.mapper.FranchiseEntityMapper;
import com.franchise.infrastructure.adapters.r2dbc.repository.FranchiseRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FranchiseAdapter implements FranchiseRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(FranchiseAdapter.class);
    private final FranchiseRepository repository;

    public FranchiseAdapter(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return repository.save(FranchiseEntityMapper.toEntity(franchise))
                .map(FranchiseEntityMapper::toDomain)
                .doFirst(() -> log.debug("Intentando persistir Franquicia en base de datos"))
                .doOnSuccess(f -> log.debug("Franquicia guardada correctamente con ID: {}", f.getId()))
                .doOnError(e -> log.error("Error al persistir Franquicia: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return repository.findById(id)
                .map(FranchiseEntityMapper::toDomain)
                .doFirst(() -> log.debug("Buscando Franquicia por ID: {}", id))
                .doOnNext(f -> log.debug("Franquicia recuperada exitosamente para el ID: {}", id));
    }
}