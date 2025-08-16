package com.ejada.oms.invoice.service;

import com.ejada.oms.invoice.entity.Invoice;
import com.ejada.oms.invoice.repository.InvoiceRepository;
import com.ejada.oms.core.service.BaseListingService;
import com.ejada.oms.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceQueryService extends BaseListingService<Invoice, Long> {

    private final InvoiceRepository invoiceRepository;

    @Override
    protected JpaRepository<Invoice, Long> getRepository() {
        return invoiceRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Invoice> getSpecificationRepository() {
        return invoiceRepository;
    }

    @Override
    protected Class<Invoice> getEntityClass() {
        return Invoice.class;
    }


    // Get invoice by ID
    public Invoice findById(Long invoiceId) {
        log.debug("Finding invoice with ID: {}", invoiceId);
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));
    }

    // Get invoice of an order
    public Invoice findByOrderId(Long orderId) {
        log.debug("Finding invoice for order ID: {}", orderId);
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for order ID: " + orderId));
    }
}