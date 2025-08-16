package com.ejada.oms.product.service;

import com.ejada.oms.product.entity.Product;
import com.ejada.oms.product.repository.ProductRepository;
import com.ejada.oms.core.service.BaseListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQueryService extends BaseListingService<Product, Long> {

    private final ProductRepository productRepository;

    @Override
    protected JpaRepository<Product, Long> getRepository() {
        return productRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Product> getSpecificationRepository() {
        return productRepository;
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }

}
