package com.ejada.oms.invoice.dto.res;

import com.ejada.oms.invoice.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceSummary {
    private Long id;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private InvoiceStatus invoiceStatus;
    private LocalDateTime invoiceDate;
}