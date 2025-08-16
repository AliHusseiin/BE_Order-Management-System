package com.ejada.oms.order.enums;

/**
 * Enumeration representing different states of an order.
 * Implements business logic for order state transitions.
 * Follows Single Responsibility Principle.
 */
public enum OrderStatus {

    PENDING("Pending", "Order has been created and is waiting for approval", 1),
    CONFIRMED("Confirmed", "Order has been approved and confirmed", 2),
    SHIPPED("Shipped", "Order has been shipped to customer", 3),
    DELIVERED("Delivered", "Order has been delivered to customer", 4),
    CANCELLED("Cancelled", "Order has been cancelled", 0);

    private final String displayName;
    private final String description;
    private final int sequence;

    OrderStatus(String displayName, String description, int sequence) {
        this.displayName = displayName;
        this.description = description;
        this.sequence = sequence;
    }


    public String getDescription() {
        return description;
    }


    /**
     * Check if status is final (no further transitions allowed)
     * @return true if status is final
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Check if status requires invoice generation
     * @return true if invoice should be generated for this status
     */
    public boolean requiresInvoice() {
        return this == CONFIRMED || this == SHIPPED || this == DELIVERED;
    }


    @Override
    public String toString() {
        return displayName;
    }
}