package com.franchise.infrastructure.adapters.r2dbc.mapper;

import com.franchise.domain.model.Branch;
import com.franchise.infrastructure.adapters.r2dbc.entity.BranchEntity;

public class BranchEntityMapper {
    public static Branch toDomain(BranchEntity entity) {
        if (entity == null) return null;
        return new Branch(
            entity.getId(), 
            entity.getName(), 
            entity.getFranchiseId()
        );
    }

    public static BranchEntity toEntity(Branch domain) {
        if (domain == null) return null;
        BranchEntity entity = new BranchEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setFranchiseId(domain.getFranchiseId());
        return entity;
    }
}