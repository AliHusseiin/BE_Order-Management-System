package com.ejada.oms.invoice.entity;

import com.ejada.oms.core.entity.BaseEntity;
import com.ejada.oms.invoice.enums.InvoiceStatus;
import com.ejada.oms.order.entity.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Invoice entity representing order invoices.
 * 
 * @author Ali Hussein
 */
@Entity
@Table(name = "invoice")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "invoice_id"))
public class Invoice extends BaseEntity<Long> {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @NotNull
    private Order order;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "invoice_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal invoiceAmount;

    @DecimalMin(value = "0.0")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "invoice_date", nullable = false)
    @Builder.Default
    private LocalDateTime invoiceDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false, length = 20)
    @Builder.Default
    private InvoiceStatus invoiceStatus = InvoiceStatus.GENERATED;

    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        this.totalAmount = invoiceAmount.add(taxAmount);
    }
}