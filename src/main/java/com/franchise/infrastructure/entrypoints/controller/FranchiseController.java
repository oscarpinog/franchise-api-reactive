package com.franchise.infrastructure.entrypoints.controller;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.FranchiseServicePort;
import com.franchise.domain.util.DomainConstants; // Importante
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchises")
@Validated
public class FranchiseController {

    private final FranchiseServicePort franchiseService;

    public FranchiseController(FranchiseServicePort franchiseService) {
        this.franchiseService = franchiseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> createFranchise(@Valid @RequestBody Franchise franchise) {
        return franchiseService.create(franchise);
    }

    @PutMapping("/{id}/name")
    public Mono<Franchise> updateName(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_FRANCHISE_ID_POSITIVE) Long id, 
            @RequestParam @NotBlank(message = DomainConstants.VALIDATION_FRANCHISE_NAME_NOT_BLANK) String name) {
        return franchiseService.updateName(id, name);
    }

    @GetMapping("/{id}/top-products")
    public Flux<Product> getTopProducts(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_FRANCHISE_ID_POSITIVE) Long id) {
        return franchiseService.getTopProducts(id);
    }

    @PostMapping("/{id}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Branch> addBranch(
            @PathVariable @Positive(message = DomainConstants.VALIDATION_FRANCHISE_ID_POSITIVE) Long id, 
            @Valid @RequestBody Branch branch) {
        return franchiseService.addBranch(id, branch);
    }
}