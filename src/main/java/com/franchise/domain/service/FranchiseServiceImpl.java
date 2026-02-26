package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Franchise;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.FranchiseServicePort;
import com.franchise.domain.ports.out.BranchOutputPort;
import com.franchise.domain.ports.out.FranchiseOutputPort;
import com.franchise.domain.ports.out.ProductOutputPort;
import com.franchise.domain.util.DomainConstants;
import com.franchise.domain.util.ValidationHelper;
import com.franchise.domain.util.ResilienceFallback; // Asegúrate de importar tu componente

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FranchiseServiceImpl implements FranchiseServicePort {

    private static final Logger log = LoggerFactory.getLogger(FranchiseServiceImpl.class);
    private static final String CB_INSTANCE = "franchiseService";

    private final FranchiseOutputPort franchiseRepository;
    private final BranchOutputPort branchRepository;
    private final ProductOutputPort productRepository;
    private final ResilienceFallback resilienceFallback;

    public FranchiseServiceImpl(FranchiseOutputPort franchiseRepository, 
                                BranchOutputPort branchRepository,
                                ProductOutputPort productRepository,
                                ResilienceFallback resilienceFallback) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.resilienceFallback = resilienceFallback;
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Mono<Franchise> create(Franchise franchise) {
        ValidationHelper.requireNotNull(franchise, "franchise");

        return Mono.just(franchise)
                .doFirst(() -> log.info(DomainConstants.LOG_FRANCHISE_CREATE_START, franchise.getName()))
                .flatMap(franchiseRepository::save)
                .doOnSuccess(f -> log.info(DomainConstants.LOG_FRANCHISE_CREATE_SUCCESS, f.getName(), f.getId()))
                .onErrorResume(resilienceFallback::handleGenericMonoError);
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Mono<Franchise> updateName(Long id, String name) {
        ValidationHelper.requireNotNull(id, "id");
        ValidationHelper.requireNotNull(name, "name");

        return franchiseRepository.findById(id)
                .doFirst(() -> log.info(DomainConstants.LOG_FRANCHISE_UPDATE_START, id, name))
                .flatMap(f -> {
                    f.setName(name);
                    return franchiseRepository.save(f);
                })
                .switchIfEmpty(ValidationHelper.onErrorNotFound(id, DomainConstants.ERROR_FRANCHISE_NOT_FOUND))
                .doOnSuccess(f -> log.info(DomainConstants.LOG_FRANCHISE_UPDATE_SUCCESS))
                .onErrorResume(resilienceFallback::handleGenericMonoError);
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Flux<Product> getTopProducts(Long franchiseId) {
        ValidationHelper.requireNotNull(franchiseId, "franchiseId");

        return franchiseRepository.findById(franchiseId)
                .doFirst(() -> log.info(DomainConstants.LOG_FRANCHISE_TOP_PRODUCTS_START, franchiseId))
                .switchIfEmpty(ValidationHelper.onErrorNotFound(franchiseId, DomainConstants.ERROR_FRANCHISE_NOT_FOUND))
                .thenMany(branchRepository.findByFranchiseId(franchiseId))
                .flatMap(branch -> {
                    log.debug(DomainConstants.LOG_FRANCHISE_TOP_PRODUCTS_DEBUG, branch.getName());
                    return productRepository.findTopByBranchIdOrderByStockDesc(branch.getId());
                })
                .doOnComplete(() -> log.info(DomainConstants.LOG_FRANCHISE_TOP_PRODUCTS_COMPLETE, franchiseId))
                .onErrorResume(resilienceFallback::handleFluxError); // Usamos el fallback de Flux para reportes
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Mono<Branch> addBranch(Long franchiseId, Branch branch) {
        ValidationHelper.requireNotNull(franchiseId, "franchiseId");
        ValidationHelper.requireNotNull(branch, "branch");

        return franchiseRepository.findById(franchiseId)
                .doFirst(() -> log.info(DomainConstants.LOG_FRANCHISE_ADD_BRANCH_VALIDATING, franchiseId, branch.getName()))
                .flatMap(f -> {
                    branch.setFranchiseId(franchiseId);
                    return branchRepository.save(branch);
                })
                .switchIfEmpty(ValidationHelper.onErrorNotFound(franchiseId, DomainConstants.ERROR_FRANCHISE_ADD_BRANCH_NOT_FOUND))
                .doOnSuccess(b -> log.info(DomainConstants.LOG_FRANCHISE_ADD_BRANCH_SUCCESS, b.getName(), franchiseId))
                .onErrorResume(resilienceFallback::handleGenericMonoError);
    }
    
    
}