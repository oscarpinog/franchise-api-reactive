package com.franchise.infrastructure.adapters.r2dbc.mapper;

import com.franchise.domain.model.Product;
import com.franchise.infrastructure.adapters.r2dbc.entity.ProductEntity;

public class ProductEntityMapper {
    public static Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return new Product(
            entity.getId(), 
            entity.getName(), 
            entity.getStock(), 
            entity.getBranchId()
        );
    }

    public static ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setStock(domain.getStock());
        entity.setBranchId(domain.getBranchId());
        return entity;
    }
}