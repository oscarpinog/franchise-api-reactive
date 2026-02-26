package com.franchise.infrastructure.adapters.r2dbc.mapper;

import com.franchise.domain.model.Product;
import com.franchise.infrastructure.adapters.r2dbc.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEntityMapperTest {

    @Test
    @DisplayName("Debe mapear ProductEntity a Product (Dominio) con todos sus campos")
    void shouldMapEntityToDomain() {
        // GIVEN
        ProductEntity entity = new ProductEntity();
        entity.setId(100L);
        entity.setName("Producto Premium");
        entity.setStock(50);
        entity.setBranchId(10L);

        // WHEN
        Product domain = ProductEntityMapper.toDomain(entity);

        // THEN
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getName()).isEqualTo(entity.getName());
        assertThat(domain.getStock()).isEqualTo(entity.getStock());
        assertThat(domain.getBranchId()).isEqualTo(entity.getBranchId());
    }

    @Test
    @DisplayName("Debe mapear Product (Dominio) a ProductEntity correctamente")
    void shouldMapDomainToEntity() {
        // GIVEN
        Product domain = new Product(200L, "Producto Estándar", 15, 5L);

        // WHEN
        ProductEntity entity = ProductEntityMapper.toEntity(domain);

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getName()).isEqualTo(domain.getName());
        assertThat(entity.getStock()).isEqualTo(domain.getStock());
        assertThat(entity.getBranchId()).isEqualTo(domain.getBranchId());
    }

    @Test
    @DisplayName("Debe retornar null cuando la entidad de producto es null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(ProductEntityMapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Debe retornar null cuando el dominio de producto es null")
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(ProductEntityMapper.toEntity(null)).isNull();
    }
}