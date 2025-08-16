package com.ejada.oms.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for data integrity constraint violations.
 * Used to provide user-friendly error messages for foreign key constraint violations.
 * 
 * @author Ali Hussein
 */
public class DataIntegrityException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String userMessage;
    private final String suggestion;

    private DataIntegrityException(String message, String errorCode, HttpStatus httpStatus, String userMessage, String suggestion) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
        this.suggestion = suggestion;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getSuggestion() {
        return suggestion;
    }

    // Factory methods for different constraint violation scenarios

    public static DataIntegrityException addressInUse(Long addressId) {
        return new DataIntegrityException(
            "Address with ID " + addressId + " is referenced by existing orders",
            "ADDRESS_IN_USE",
            HttpStatus.CONFLICT,
            "Cannot delete address - it is currently used in existing orders",
            "Please remove or update the orders that use this address before deleting it"
        );
    }

    public static DataIntegrityException customerHasOrders(Long customerId) {
        return new DataIntegrityException(
            "Customer with ID " + customerId + " has existing orders",
            "CUSTOMER_HAS_ORDERS", 
            HttpStatus.CONFLICT,
            "Cannot delete customer - they have existing orders in the system",
            "Please delete or reassign the customer's orders before deleting the customer"
        );
    }

    public static DataIntegrityException productInUse(Long productId) {
        return new DataIntegrityException(
            "Product with ID " + productId + " is referenced in existing order items",
            "PRODUCT_IN_USE",
            HttpStatus.CONFLICT,
            "Cannot delete product - it is used in existing orders",
            "Products that have been ordered cannot be deleted to maintain order history integrity"
        );
    }

    public static DataIntegrityException userHasCreatedOrders(Long userId) {
        return new DataIntegrityException(
            "User with ID " + userId + " has created orders in the system",
            "USER_HAS_ORDERS",
            HttpStatus.CONFLICT,
            "Cannot delete user - they have created orders in the system", 
            "Please reassign the orders to another user before deleting this user"
        );
    }

    public static DataIntegrityException customerHasAddresses(Long customerId) {
        return new DataIntegrityException(
            "Customer with ID " + customerId + " has associated addresses",
            "CUSTOMER_HAS_ADDRESSES",
            HttpStatus.CONFLICT,
            "Cannot delete customer - they have associated addresses",
            "Please delete the customer's addresses first before deleting the customer"
        );
    }

    public static DataIntegrityException genericConstraintViolation(String constraintName) {
        return new DataIntegrityException(
            "Data integrity constraint violation: " + constraintName,
            "CONSTRAINT_VIOLATION",
            HttpStatus.CONFLICT,
            "Cannot complete operation - it would violate data integrity rules",
            "Please ensure all related data is properly handled before performing this operation"
        );
    }

    /**
     * Parse PostgreSQL constraint violation message and create appropriate exception
     */
    public static DataIntegrityException fromConstraintViolation(String message) {
        if (message == null) {
            return genericConstraintViolation("unknown");
        }

        // Extract constraint name from PostgreSQL error message
        // Format: "violates foreign key constraint "constraint_name""
        if (message.contains("fk_order_address")) {
            // Extract address ID if possible
            return DataIntegrityException.addressInUse(null);
        } else if (message.contains("fk_order_customer")) {
            return DataIntegrityException.customerHasOrders(null);
        } else if (message.contains("fk_order_item_product")) {
            return DataIntegrityException.productInUse(null);
        } else if (message.contains("fk_order_created_by_user")) {
            return DataIntegrityException.userHasCreatedOrders(null);
        } else if (message.contains("fk_address_customer")) {
            return DataIntegrityException.customerHasAddresses(null);
        } else {
            // Extract constraint name from message if possible
            String constraintName = extractConstraintName(message);
            return genericConstraintViolation(constraintName);
        }
    }

    private static String extractConstraintName(String message) {
        // Try to extract constraint name from PostgreSQL error message
        // Pattern: violates foreign key constraint "constraint_name"
        int startIndex = message.indexOf("constraint \"");
        if (startIndex != -1) {
            startIndex += 12; // length of "constraint \""
            int endIndex = message.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return message.substring(startIndex, endIndex);
            }
        }
        return "unknown_constraint";
    }
}