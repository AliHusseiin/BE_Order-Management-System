package com.ejada.oms.invoice.dto.res;

import com.ejada.oms.invoice.enums.InvoiceStatus;
import com.ejada.oms.order.dto.res.OrderSummary;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private BigDecimal invoiceAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime invoiceDate;
    private InvoiceStatus invoiceStatus;
    private OrderSummary order;
}