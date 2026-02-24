package com.franchise.infrastructure.adapters.r2dbc.mapper;

import com.franchise.domain.model.Franchise;
import com.franchise.infrastructure.adapters.r2dbc.entity.FranchiseEntity;

public class FranchiseEntityMapper {
    public static Franchise toDomain(FranchiseEntity entity) {
        if (entity == null) return null;
        return new Franchise(entity.getId(), entity.getName());
    }

    public static FranchiseEntity toEntity(Franchise domain) {
        if (domain == null) return null;
        return new FranchiseEntity(domain.getId(), domain.getName());
    }
}