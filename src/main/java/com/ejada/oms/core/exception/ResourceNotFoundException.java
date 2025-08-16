package com.ejada.oms.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Follows Single Responsibility Principle - handles only resource not found scenarios.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;
    private final String errorCode;

    /**
     * Constructor with resource type and ID
     * @param resourceType type of resource (e.g., "Order", "Customer")
     * @param resourceId ID of the resource
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s not found with ID: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    /**
     * Constructor with resource type, ID and custom message
     * @param resourceType type of resource
     * @param resourceId ID of the resource
     * @param message custom error message
     */
    public ResourceNotFoundException(String resourceType, String resourceId, String message) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    /**
     * Constructor with custom message only
     * @param message error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = "Unknown";
        this.resourceId = "Unknown";
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    // Static factory methods for common scenarios

    /**
     * Create exception for order not found
     * @param orderId order ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException orderNotFound(Long orderId) {
        return new ResourceNotFoundException("Order", orderId.toString());
    }

    /**
     * Create exception for customer not found
     * @param customerId customer ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException customerNotFound(Long customerId) {
        return new ResourceNotFoundException("Customer", customerId.toString());
    }

    /**
     * Create exception for product not found
     * @param productId product ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException productNotFound(Long productId) {
        return new ResourceNotFoundException("Product", productId.toString());
    }

    /**
     * Create exception for user not found
     * @param userId user ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFound(Long userId) {
        return new ResourceNotFoundException("User", userId.toString());
    }

    /**
     * Create exception for user not found by username
     * @param username username
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFoundByUsername(String username) {
        return new ResourceNotFoundException("User", username,
                String.format("User not found with username: %s", username));
    }

    /**
     * Create exception for invoice not found
     * @param invoiceId invoice ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException invoiceNotFound(Long invoiceId) {
        return new ResourceNotFoundException("Invoice", invoiceId.toString());
    }

    /**
     * Create exception for invoice not found by order
     * @param orderId order ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException invoiceNotFoundForOrder(Long orderId) {
        return new ResourceNotFoundException("Invoice", orderId.toString(),
                String.format("Invoice not found for order ID: %s", orderId));
    }

    /**
     * Create exception for address not found
     * @param addressId address ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException addressNotFound(Long addressId) {
        return new ResourceNotFoundException("Address", addressId.toString());
    }
}