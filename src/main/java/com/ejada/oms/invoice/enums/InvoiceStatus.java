package com.ejada.oms.invoice.enums;

/**
 * Enumeration representing different states of an invoice.
 * Implements business logic for invoice state transitions.
 * Follows Single Responsibility Principle.
 */
public enum InvoiceStatus {

    GENERATED("Generated", "Invoice has been generated but not sent", 1),
    SENT("Sent", "Invoice has been sent to customer", 2),
    PAID("Paid", "Invoice has been paid by customer", 3),
    OVERDUE("Overdue", "Invoice payment is overdue", 4),
    CANCELLED("Cancelled", "Invoice has been cancelled", 0);

    private final String displayName;
    private final String description;
    private final int sequence;

    InvoiceStatus(String displayName, String description, int sequence) {
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
        return this == PAID || this == CANCELLED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}