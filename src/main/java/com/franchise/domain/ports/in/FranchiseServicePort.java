package com.franchise.domain.ports.in;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {
    Mono<Franchise> create(Franchise franchise);
    Mono<Franchise> updateName(Long id, String name);
    Flux<Product> getTopProducts(Long franchiseId); // Cambiado para que coincida con el controller
    Mono<Branch> addBranch(Long franchiseId, Branch branch); // Agregamos este
}