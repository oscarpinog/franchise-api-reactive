package com.franchise.domain.ports.in;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    // Para crear la sucursal (usado desde FranchiseController)
    Mono<Branch> addBranch(Long franchiseId, Branch branch);
    
    // Para actualizar el nombre de la sucursal
    Mono<Branch> updateName(Long id, String name);
    
    // Para agregar productos a esta sucursal (lo que pide tu controller)
    Mono<Product> addProduct(Long branchId, Product product);
}