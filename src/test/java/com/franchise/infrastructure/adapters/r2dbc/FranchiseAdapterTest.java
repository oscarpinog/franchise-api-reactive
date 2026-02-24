package com.franchise.infrastructure.adapters.r2dbc;

import com.franchise.domain.model.Franchise;
import com.franchise.infrastructure.adapters.r2dbc.entity.FranchiseEntity;
import com.franchise.infrastructure.adapters.r2dbc.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseAdapterTest {

    @Mock
    private FranchiseRepository repository;

    @InjectMocks
    private FranchiseAdapter franchiseAdapter;

    private Franchise domainFranchise;
    private FranchiseEntity franchiseEntity;

    @BeforeEach
    void setUp() {
        // Objeto de Dominio
        domainFranchise = new Franchise();
        domainFranchise.setName("Franquicia Horizonte");

        // Objeto de Base de Datos (Entity)
        franchiseEntity = new FranchiseEntity();
        franchiseEntity.setId(1L);
        franchiseEntity.setName("Franquicia Horizonte");
    }

    @Test
    @DisplayName("Debe persistir una franquicia y devolver el objeto de dominio con ID")
    void save_Success() {
        // GIVEN
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(franchiseEntity));

        // WHEN
        Mono<Franchise> result = franchiseAdapter.save(domainFranchise);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(f -> f.getId().equals(1L) && f.getName().equals("Franquicia Horizonte"))
                .verifyComplete();

        verify(repository, times(1)).save(any(FranchiseEntity.class));
    }

    @Test
    @DisplayName("Debe buscar una franquicia por ID y mapearla correctamente")
    void findById_Success() {
        // GIVEN
        when(repository.findById(1L)).thenReturn(Mono.just(franchiseEntity));

        // WHEN
        Mono<Franchise> result = franchiseAdapter.findById(1L);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(f -> f.getId().equals(1L) && f.getName().equals("Franquicia Horizonte"))
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Debe devolver vacío si la franquicia no existe en la base de datos")
    void findById_Empty() {
        // GIVEN
        when(repository.findById(99L)).thenReturn(Mono.empty());

        // WHEN
        Mono<Franchise> result = franchiseAdapter.findById(99L);

        // THEN
        StepVerifier.create(result)
                .verifyComplete(); // Un Mono vacío termina sin emitir nada pero con señal de complete
    }
}