package com.franchise.infrastructure.adapters.r2dbc.mapper;

import com.franchise.domain.model.Branch;
import com.franchise.infrastructure.adapters.r2dbc.entity.BranchEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BranchEntityMapperTest {

    @Test
    @DisplayName("Debe mapear de Entidad a Dominio correctamente")
    void shouldMapEntityToDomain() {
        // GIVEN
        BranchEntity entity = new BranchEntity();
        entity.setId(10L);
        entity.setName("Sucursal Centro");
        entity.setFranchiseId(1L);

        // WHEN
        Branch domain = BranchEntityMapper.toDomain(entity);

        // THEN
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getName()).isEqualTo(entity.getName());
        assertThat(domain.getFranchiseId()).isEqualTo(entity.getFranchiseId());
    }

    @Test
    @DisplayName("Debe mapear de Dominio a Entidad correctamente")
    void shouldMapDomainToEntity() {
        // GIVEN
        Branch domain = new Branch(5L, "Sucursal Norte", 2L);

        // WHEN
        BranchEntity entity = BranchEntityMapper.toEntity(domain);

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getName()).isEqualTo(domain.getName());
        assertThat(entity.getFranchiseId()).isEqualTo(domain.getFranchiseId());
    }

    @Test
    @DisplayName("Debe retornar null cuando la entidad es null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(BranchEntityMapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Debe retornar null cuando el objeto de dominio es null")
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(BranchEntityMapper.toEntity(null)).isNull();
    }
}