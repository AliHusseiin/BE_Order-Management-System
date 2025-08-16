package com.ejada.oms.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST; // Default status
    }

    public BusinessException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    // =========================================================================
    // AUTHENTICATION & SECURITY EXCEPTIONS
    // =========================================================================

    public static BusinessException invalidCredentials() {
        return new BusinessException("INVALID_CREDENTIALS", "Invalid username or password");
    }

    public static BusinessException userNotAuthenticated() {
        return new BusinessException("NOT_AUTHENTICATED", "User not authenticated");
    }

    public static BusinessException userInactive() {
        return new BusinessException("USER_INACTIVE", "User account is inactive");
    }

    public static BusinessException tokenExpired() {
        return new BusinessException("TOKEN_EXPIRED", "JWT token has expired");
    }

    public static BusinessException invalidToken() {
        return new BusinessException("INVALID_TOKEN", "Invalid JWT token");
    }

    public static BusinessException accessDenied(String message) {
        return new BusinessException("ACCESS_DENIED", message);
    }

    // =========================================================================
    // USER MANAGEMENT EXCEPTIONS
    // =========================================================================

    public static BusinessException userNotFound(String username) {
        return new BusinessException("USER_NOT_FOUND", "User not found: " + username);
    }

    public static BusinessException userNotFound(Long id) {
        return new BusinessException("USER_NOT_FOUND", "User not found with ID: " + id);
    }

    public static BusinessException usernameAlreadyExists(String username) {
        return new BusinessException("USERNAME_EXISTS", "Username already exists: " + username);
    }

    public static BusinessException emailAlreadyExists(String email) {
        return new BusinessException("EMAIL_EXISTS", "Email already exists: " + email);
    }

    // =========================================================================
    // CUSTOMER MANAGEMENT EXCEPTIONS
    // =========================================================================

    public static BusinessException customerNotFound(Long id) {
        return new BusinessException("CUSTOMER_NOT_FOUND", "Customer not found with ID: " + id);
    }

    public static BusinessException mobileAlreadyExists(String mobile) {
        return new BusinessException("MOBILE_EXISTS", "Mobile number already exists: " + mobile);
    }

    // =========================================================================
    // PRODUCT MANAGEMENT EXCEPTIONS
    // =========================================================================

    public static BusinessException productNotFound(Long id) {
        return new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + id);
    }

    public static BusinessException productNameAlreadyExists(String productName) {
        return new BusinessException("PRODUCT_NAME_EXISTS", "Product name already exists: " + productName);
    }

    public static BusinessException insufficientStock(String productName, Integer available, Integer requested) {
        return new BusinessException("INSUFFICIENT_STOCK",
                String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                        productName, available, requested));
    }

    // =========================================================================
    // ORDER MANAGEMENT EXCEPTIONS
    // =========================================================================

    public static BusinessException orderNotFound(Long id) {
        return new BusinessException("ORDER_NOT_FOUND", "Order not found with ID: " + id);
    }

    public static BusinessException invalidOrderStatus(String message) {
        return new BusinessException("INVALID_ORDER_STATUS", message);
    }

    // =========================================================================
    // INVOICE MANAGEMENT EXCEPTIONS
    // =========================================================================

    public static BusinessException invoiceNotFound(Long id) {
        return new BusinessException("INVOICE_NOT_FOUND", "Invoice not found with ID: " + id);
    }

    public static BusinessException invoiceAlreadyExists(Long orderId) {
        return new BusinessException("INVOICE_EXISTS", "Invoice already exists for order ID: " + orderId);
    }

    // =========================================================================
    // VALIDATION EXCEPTIONS
    // =========================================================================

    public static BusinessException validationError(String message) {
        return new BusinessException("VALIDATION_ERROR", message);
    }

    public static BusinessException requiredFieldMissing(String fieldName) {
        return new BusinessException("REQUIRED_FIELD_MISSING", "Required field missing: " + fieldName);
    }
}
