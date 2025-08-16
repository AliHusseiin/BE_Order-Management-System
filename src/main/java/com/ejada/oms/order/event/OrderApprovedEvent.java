package com.ejada.oms.order.event;

import com.ejada.oms.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Event published when an order is approved.
 * 
 * @author Ali Hussein
 */
@Getter
public class OrderApprovedEvent {
    
    private final Order order;
    private final LocalDateTime timestamp;
    
    public OrderApprovedEvent(Order order) {
        this.order = order;
        this.timestamp = LocalDateTime.now();
    }
}