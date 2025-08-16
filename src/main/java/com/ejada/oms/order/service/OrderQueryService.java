package com.ejada.oms.order.service;

import com.ejada.oms.order.entity.Order;
import com.ejada.oms.order.repository.OrderRepository;
import com.ejada.oms.core.service.BaseListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderQueryService extends BaseListingService<Order, Long> {

    private final OrderRepository orderRepository;

    @Override
    protected JpaRepository<Order, Long> getRepository() {
        return orderRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Order> getSpecificationRepository() {
        return orderRepository;
    }

    @Override
    protected Class<Order> getEntityClass() {
        return Order.class;
    }


    // Get orders of a customer
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        log.debug("Finding orders for customer ID: {}", customerId);
        return orderRepository.findByCustomerId(customerId, pageable);
    }
}