package com.franchise.infrastructure.adapters.r2dbc.repository;

import com.franchise.infrastructure.adapters.r2dbc.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
    Flux<BranchEntity> findByFranchiseId(Long franchiseId);
}