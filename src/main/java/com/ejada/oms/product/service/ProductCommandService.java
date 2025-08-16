package com.ejada.oms.product.service;

import com.ejada.oms.product.entity.Product;
import com.ejada.oms.product.dto.req.ProductCreateRequest;
import com.ejada.oms.product.repository.ProductRepository;
import com.ejada.oms.core.exception.BusinessException;
import com.ejada.oms.core.exception.DataIntegrityException;
import com.ejada.oms.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for product command operations.
 * 
 * @author Ali Hussein
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Product create(ProductCreateRequest request) {
        log.info("Creating product: {}", request.getProductName());

        if (productRepository.existsByProductName(request.getProductName())) {
            throw BusinessException.productNameAlreadyExists(request.getProductName());
        }

        Product product = Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return saved;
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> BusinessException.productNotFound(id));
    }

    public void delete(Long id) {
        Product product = findById(id);
        
        // Check if product is used in any order items before deletion
        if (orderRepository.existsByProductId(id)) {
            throw DataIntegrityException.productInUse(id);
        }
        
        log.info("Deleting product: {}", id);
        productRepository.delete(product);
        log.info("Product deleted: {}", id);
    }
}