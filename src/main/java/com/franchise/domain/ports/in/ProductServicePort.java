package com.franchise.domain.ports.in;

import com.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    // Nota: addProduct se suele llamar desde BranchController, 
    // pero lo dejamos aquí por si necesitas lógica atómica de producto.
    Mono<Product> addProduct(Long branchId, Product product);
    
    Mono<Product> updateStock(Long id, Integer stock);
    
    Mono<Product> updateName(Long id, String name);
    
    Mono<Void> delete(Long id);
}