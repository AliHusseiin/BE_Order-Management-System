package com.ejada.oms.order.event;

import com.ejada.oms.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event handler for order-related events.
 * 
 * @author Ali Hussein
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {
    
    private final InvoiceService invoiceService;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Order created event received for Order ID: {} - Customer: {}", 
                event.getOrder().getId(), 
                event.getOrder().getCustomer().getFullName());
        
        // Demo: In real application, this would send email notification
        log.info("Email notification would be sent to: {}", 
                event.getOrder().getCustomer().getUser().getEmail());
    }
    
    @EventListener
    public void handleOrderApproved(OrderApprovedEvent event) {
        log.info("Order approved event received for Order ID: {} - Generating invoice", 
                event.getOrder().getId());
        
        try {
            invoiceService.generateInvoiceForOrder(event.getOrder());
            log.info("Invoice generated successfully for Order ID: {}", event.getOrder().getId());
        } catch (Exception e) {
            log.error("Failed to generate invoice for Order ID: {} - Error: {}", 
                    event.getOrder().getId(), e.getMessage());
        }
    }
}