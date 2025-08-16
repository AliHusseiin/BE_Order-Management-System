package com.ejada.oms.order.event;

import com.ejada.oms.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Event published when an order is created.
 * 
 * @author Ali Hussein
 */
@Getter
public class OrderCreatedEvent {
    
    private final Order order;
    private final LocalDateTime timestamp;
    
    public OrderCreatedEvent(Order order) {
        this.order = order;
        this.timestamp = LocalDateTime.now();
    }
}