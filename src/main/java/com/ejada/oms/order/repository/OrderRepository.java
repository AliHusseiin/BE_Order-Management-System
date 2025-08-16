package com.ejada.oms.order.repository;

import com.ejada.oms.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Order entity operations.
 * 
 * @author Ali Hussein
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    boolean existsByCustomerId(Long customerId);
    
    boolean existsByShippingAddressId(Long addressId);
    
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    boolean existsByProductId(@Param("productId") Long productId);
}
