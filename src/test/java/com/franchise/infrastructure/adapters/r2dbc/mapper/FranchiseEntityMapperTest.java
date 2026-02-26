package com.franchise.infrastructure.adapters.r2dbc.mapper;

import com.franchise.domain.model.Franchise;
import com.franchise.infrastructure.adapters.r2dbc.entity.FranchiseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FranchiseEntityMapperTest {

    @Test
    @DisplayName("Debe mapear FranchiseEntity a Franchise (Dominio)")
    void shouldMapEntityToDomain() {
        // GIVEN
        FranchiseEntity entity = new FranchiseEntity(1L, "Franquicia Bogota");

        // WHEN
        Franchise domain = FranchiseEntityMapper.toDomain(entity);

        // THEN
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getName()).isEqualTo(entity.getName());
    }

    @Test
    @DisplayName("Debe mapear Franchise (Dominio) a FranchiseEntity")
    void shouldMapDomainToEntity() {
        // GIVEN
        Franchise domain = new Franchise(2L, "Franquicia Medellin");

        // WHEN
        FranchiseEntity entity = FranchiseEntityMapper.toEntity(domain);

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getName()).isEqualTo(domain.getName());
    }

    @Test
    @DisplayName("Debe retornar null cuando la entidad de franquicia es null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(FranchiseEntityMapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Debe retornar null cuando el dominio de franquicia es null")
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(FranchiseEntityMapper.toEntity(null)).isNull();
    }
}