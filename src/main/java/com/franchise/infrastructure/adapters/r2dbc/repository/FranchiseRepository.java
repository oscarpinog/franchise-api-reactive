package com.franchise.infrastructure.adapters.r2dbc.repository;

import com.franchise.infrastructure.adapters.r2dbc.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
}