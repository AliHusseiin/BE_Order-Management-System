package com.ejada.oms.order.service;

import com.ejada.oms.order.entity.Order;
import com.ejada.oms.order.entity.OrderItem;
import com.ejada.oms.order.dto.req.OrderCreateRequest;
import com.ejada.oms.order.repository.OrderRepository;
import com.ejada.oms.customer.entity.Customer;
import com.ejada.oms.customer.service.CustomerCommandService;
import com.ejada.oms.product.entity.Product;
import com.ejada.oms.product.service.ProductCommandService;
import com.ejada.oms.auth.entity.User;
import com.ejada.oms.core.util.SecurityUtils;
import com.ejada.oms.core.exception.BusinessException;
import com.ejada.oms.order.event.OrderCreatedEvent;
import com.ejada.oms.order.event.OrderApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for order command operations with event-driven processing.
 * 
 * @author Ali Hussein
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final CustomerCommandService customerCommandService;
    private final ProductCommandService productCommandService;
    private final ApplicationEventPublisher eventPublisher;

    public Order create(OrderCreateRequest request) {
        log.info("Admin creating order for customer ID: {}", request.getCustomerId());

        User currentUser = SecurityUtils.getCurrentUser();
        Customer customer = customerCommandService.findById(request.getCustomerId());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var itemRequest : request.getOrderItems()) {
            Product product = productCommandService.findById(itemRequest.getProductId());

            if (!product.hasStock(itemRequest.getQuantity())) {
                throw BusinessException.insufficientStock(product.getProductName(),
                        product.getStockQuantity(), itemRequest.getQuantity());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);

            product.reduceStock(itemRequest.getQuantity());
        }

        Order order = Order.builder()
                .customer(customer)
                .shippingAddress(customer.getDefaultAddress())
                .createdByUser(currentUser)
                .totalAmount(totalAmount)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        Order saved = orderRepository.save(order);
        log.info("Order created with ID: {}, Status: PENDING", saved.getId());
        
        // Publish order created event
        eventPublisher.publishEvent(new OrderCreatedEvent(saved));
        
        return saved;
    }

    public Order approve(Long orderId) {
        log.info("Admin approving order ID: {}", orderId);

        Order order = findById(orderId);

        if (!order.canBeApproved()) {
            throw BusinessException.invalidOrderStatus("Order cannot be approved in status: " + order.getOrderStatus());
        }

        order.approve();
        Order saved = orderRepository.save(order);

        // Publish order approved event (will trigger invoice generation)
        eventPublisher.publishEvent(new OrderApprovedEvent(saved));

        log.info("Order approved, Status: CONFIRMED");
        return saved;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.orderNotFound(id));
    }
}