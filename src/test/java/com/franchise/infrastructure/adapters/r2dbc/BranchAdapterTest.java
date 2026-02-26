package com.franchise.infrastructure.adapters.r2dbc;

import com.franchise.domain.model.Branch;
import com.franchise.infrastructure.adapters.r2dbc.entity.BranchEntity;
import com.franchise.infrastructure.adapters.r2dbc.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchAdapterTest {

    @Mock
    private BranchRepository repository;

    @InjectMocks
    private BranchAdapter branchAdapter;

    private Branch domainBranch;
    private BranchEntity branchEntity;

    @BeforeEach
    void setUp() {
        // Objeto de dominio para la entrada
        domainBranch = new Branch();
        domainBranch.setName("Sucursal Test");
        domainBranch.setFranchiseId(1L);

        // Entidad que devolvería la DB
        branchEntity = new BranchEntity();
        branchEntity.setId(10L);
        branchEntity.setName("Sucursal Test");
        branchEntity.setFranchiseId(1L);
    }

    @Test
    @DisplayName("Debe guardar y mapear una sucursal correctamente")
    void save_Success() {
        // GIVEN: El repositorio devuelve la entidad persistida
        when(repository.save(any(BranchEntity.class))).thenReturn(Mono.just(branchEntity));

        // WHEN
        Mono<Branch> result = branchAdapter.save(domainBranch);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(b -> b.getId().equals(10L) && b.getName().equals("Sucursal Test"))
                .verifyComplete();

        verify(repository).save(any(BranchEntity.class));
    }

    @Test
    @DisplayName("Debe buscar por ID y mapear a dominio")
    void findById_Success() {
        when(repository.findById(10L)).thenReturn(Mono.just(branchEntity));

        Mono<Branch> result = branchAdapter.findById(10L);

        StepVerifier.create(result)
                .expectNextMatches(b -> b.getId().equals(10L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe buscar por Franquicia ID y devolver un Flux mapeado")
    void findByFranchiseId_Success() {
        when(repository.findByFranchiseId(1L)).thenReturn(Flux.just(branchEntity));

        Flux<Branch> result = branchAdapter.findByFranchiseId(1L);

        StepVerifier.create(result)
                .expectNextMatches(b -> b.getFranchiseId().equals(1L))
                .verifyComplete();
    }


}