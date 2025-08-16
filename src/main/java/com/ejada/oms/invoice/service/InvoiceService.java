package com.ejada.oms.invoice.service;

import com.ejada.oms.invoice.entity.Invoice;
import com.ejada.oms.invoice.repository.InvoiceRepository;
import com.ejada.oms.order.entity.Order;
import com.ejada.oms.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice generateInvoiceForOrder(Order order) {
        log.info("Auto-generating invoice for order ID: {}", order.getId());

        if (invoiceRepository.existsByOrderId(order.getId())) {
            throw BusinessException.invoiceAlreadyExists(order.getId());
        }

        BigDecimal invoiceAmount = order.getTotalAmount();
        BigDecimal taxRate = new BigDecimal("0.08"); // 8% tax rate
        BigDecimal taxAmount = invoiceAmount.multiply(taxRate);
        BigDecimal totalAmount = invoiceAmount.add(taxAmount);

        Invoice invoice = Invoice.builder()
                .order(order)
                .invoiceAmount(invoiceAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated with ID: {}", saved.getId());
        return saved;
    }
}
