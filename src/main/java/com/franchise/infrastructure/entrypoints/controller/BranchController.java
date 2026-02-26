package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.BranchServicePort;
import com.franchise.domain.util.DomainConstants; // Importamos las constantes
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branches")
@Validated
public class BranchController {

    private final BranchServicePort branchService;

    public BranchController(BranchServicePort branchService) {
        this.branchService = branchService;
    }

    @PostMapping("/{id}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> addProduct(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_BRANCH_ID_POSITIVE) Long id, 
            @Valid @RequestBody Product product) {
        
        return branchService.addProduct(id, product);
    }

    @PutMapping("/{id}/name")
    public Mono<Branch> updateBranchName(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_ID_POSITIVE) Long id, 
            @RequestParam @NotBlank(message = DomainConstants.VALIDATION_NAME_NOT_BLANK) String name) {
        
        return branchService.updateName(id, name);
    }
}