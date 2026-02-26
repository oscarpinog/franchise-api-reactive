package com.franchise.domain.service;

import com.franchise.domain.model.Branch;
import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.BranchServicePort;
import com.franchise.domain.ports.out.BranchOutputPort;
import com.franchise.domain.ports.out.ProductOutputPort;
import com.franchise.domain.util.DomainConstants;
import com.franchise.domain.util.ResilienceFallback;
import com.franchise.domain.util.ValidationHelper;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BranchServiceImpl implements BranchServicePort {

    private static final Logger log = LoggerFactory.getLogger(BranchServiceImpl.class);
    private static final String CB_INSTANCE = "franchiseService";
    
    private final BranchOutputPort branchRepository;
    private final ProductOutputPort productRepository;
    private final ResilienceFallback resilienceFallback;

    public BranchServiceImpl(BranchOutputPort branchRepository, 
                             ProductOutputPort productRepository,
                             ResilienceFallback resilienceFallback) {
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.resilienceFallback = resilienceFallback;
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Mono<Branch> addBranch(Long franchiseId, Branch branch) {
        ValidationHelper.requireNotNull(franchiseId, "franchiseId");
        ValidationHelper.requireNotNull(branch, "branch");

        return Mono.just(branch)
                .doFirst(() -> log.info(DomainConstants.LOG_BRANCH_ADD_START, branch.getName(), franchiseId))
                .flatMap(b -> {
                    b.setFranchiseId(franchiseId);
                    return branchRepository.save(b);
                })
                .doOnSuccess(saved -> log.info(DomainConstants.LOG_BRANCH_ADD_SUCCESS, saved.getId()))
                .onErrorResume(resilienceFallback::handleGenericMonoError);
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Mono<Branch> updateName(Long id, String name) {
        ValidationHelper.requireNotNull(id, "id");
        ValidationHelper.requireNotNull(name, "name");

        return branchRepository.findById(id)
                .doFirst(() -> log.info(DomainConstants.LOG_BRANCH_UPDATE_START, id, name))
                .flatMap(branch -> {
                    branch.rename(name);
                    return branchRepository.save(branch);
                })
                .switchIfEmpty(ValidationHelper.onErrorNotFound(id, DomainConstants.ERROR_BRANCH_NOT_FOUND))
                .doOnSuccess(updated -> log.info(DomainConstants.LOG_BRANCH_UPDATE_SUCCESS))
                .onErrorResume(resilienceFallback::handleGenericMonoError);
    }

    @Override
    @RateLimiter(name = CB_INSTANCE)
    @CircuitBreaker(name = CB_INSTANCE)
    public Mono<Product> addProduct(Long branchId, Product product) {
        ValidationHelper.requireNotNull(branchId, "branchId");
        ValidationHelper.requireNotNull(product, "product");

        return branchRepository.findById(branchId)
                .doFirst(() -> log.info(DomainConstants.LOG_PRODUCT_ADD_VALIDATING, branchId, product.getName()))
                .flatMap(branch -> {
                    product.setBranchId(branchId);
                    return productRepository.save(product);
                })
                .switchIfEmpty(ValidationHelper.onErrorNotFound(branchId, DomainConstants.ERROR_PRODUCT_BRANCH_NOT_FOUND))
                .doOnSuccess(p -> log.info(DomainConstants.LOG_PRODUCT_ADD_SUCCESS, p.getName(), branchId))
                .doOnError(e -> log.error(DomainConstants.LOG_PRODUCT_ADD_ERROR, e.getMessage()))
                .onErrorResume(resilienceFallback::handleGenericMonoError);
    }
}