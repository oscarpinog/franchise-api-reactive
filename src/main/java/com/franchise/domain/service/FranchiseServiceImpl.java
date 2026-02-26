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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FranchiseServiceImpl implements FranchiseServicePort {

    private static final Logger log = LoggerFactory.getLogger(FranchiseServiceImpl.class);

    private final FranchiseOutputPort franchiseRepository;
    private final BranchOutputPort branchRepository;
    private final ProductOutputPort productRepository;

    public FranchiseServiceImpl(FranchiseOutputPort franchiseRepository, 
                                BranchOutputPort branchRepository,
                                ProductOutputPort productRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        ValidationHelper.requireNotNull(franchise, "franchise");

        return Mono.just(franchise)
                .doFirst(() -> log.info(DomainConstants.LOG_FRANCHISE_CREATE_START, franchise.getName()))
                .flatMap(franchiseRepository::save)
                .doOnSuccess(f -> log.info(DomainConstants.LOG_FRANCHISE_CREATE_SUCCESS, f.getName(), f.getId()));
    }

    @Override
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
                .doOnSuccess(f -> log.info(DomainConstants.LOG_FRANCHISE_UPDATE_SUCCESS));
    }

    @Override
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
                .doOnComplete(() -> log.info(DomainConstants.LOG_FRANCHISE_TOP_PRODUCTS_COMPLETE, franchiseId));
    }

    @Override
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
                .doOnSuccess(b -> log.info(DomainConstants.LOG_FRANCHISE_ADD_BRANCH_SUCCESS, b.getName(), franchiseId));
    }
}