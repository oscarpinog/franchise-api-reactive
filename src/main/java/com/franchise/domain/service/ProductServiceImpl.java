package com.franchise.domain.service;

import com.franchise.domain.model.Product;
import com.franchise.domain.ports.in.ProductServicePort;
import com.franchise.domain.ports.out.ProductOutputPort;
import com.franchise.domain.util.DomainConstants;
import com.franchise.domain.util.ValidationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductServicePort {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductOutputPort productRepository;

    public ProductServiceImpl(ProductOutputPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Product> addProduct(Long branchId, Product product) {
        ValidationHelper.requireNotNull(branchId, "branchId");
        ValidationHelper.requireNotNull(product, "product");

        return Mono.just(product)
                .doFirst(() -> log.info(DomainConstants.LOG_PRODUCT_ADD_START, product.getName(), branchId))
                .flatMap(p -> {
                    p.setBranchId(branchId);
                    return productRepository.save(p);
                })
                .doOnSuccess(saved -> log.info(DomainConstants.LOG_PRODUCT_ADD_SUCC, saved.getId()));
    }

    @Override
    public Mono<Product> updateStock(Long id, Integer stock) {
        ValidationHelper.requireNotNull(id, "id");
        ValidationHelper.requireNotNull(stock, "stock");

        return productRepository.findById(id)
                .doFirst(() -> log.info(DomainConstants.LOG_PRODUCT_STOCK_UPDATE_START, id, stock))
                .flatMap(p -> {
                    p.setStock(stock);
                    return productRepository.save(p);
                })
                .switchIfEmpty(ValidationHelper.onErrorNotFound(id, DomainConstants.ERROR_PRODUCT_NOT_FOUND))
                .doOnSuccess(p -> log.info(DomainConstants.LOG_PRODUCT_STOCK_UPDATE_SUCCESS, p.getName()));
    }

    @Override
    public Mono<Product> updateName(Long id, String name) {
        ValidationHelper.requireNotNull(id, "id");
        ValidationHelper.requireNotNull(name, "name");

        return productRepository.findById(id)
                .doFirst(() -> log.info(DomainConstants.LOG_PRODUCT_NAME_UPDATE_START, id, name))
                .flatMap(p -> {
                    p.setName(name);
                    return productRepository.save(p);
                })
                .switchIfEmpty(ValidationHelper.onErrorNotFound(id, DomainConstants.ERROR_PRODUCT_NOT_FOUND))
                .doOnSuccess(p -> log.info(DomainConstants.LOG_PRODUCT_NAME_UPDATE_SUCCESS, id));
    }

    @Override
    public Mono<Void> delete(Long id) {
        ValidationHelper.requireNotNull(id, "id");

        return productRepository.findById(id)
                .doFirst(() -> log.info(DomainConstants.LOG_PRODUCT_DELETE_START, id))
                .switchIfEmpty(ValidationHelper.onErrorNotFound(id, DomainConstants.ERROR_PRODUCT_DELETE_NOT_FOUND))
                .flatMap(product -> productRepository.deleteById(product.getId()))
                .doOnSuccess(v -> log.info(DomainConstants.LOG_PRODUCT_DELETE_SUCCESS, id));
    }
}