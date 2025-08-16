package com.ejada.oms.invoice.mapper;

import com.ejada.oms.invoice.dto.res.InvoiceResponse;
import com.ejada.oms.invoice.dto.res.InvoiceSummary;
import com.ejada.oms.invoice.entity.Invoice;
import com.ejada.oms.order.dto.res.OrderSummary;
import com.ejada.oms.order.entity.Order;
import com.ejada.oms.customer.dto.res.CustomerSummary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "order", target = "order")
    InvoiceResponse toResponse(Invoice invoice);

    InvoiceSummary toSummary(Invoice invoice);

    // Manual mapping for OrderSummary to break circular dependency
    default OrderSummary orderToOrderSummary(Order order) {
        if (order == null) return null;
        
        OrderSummary summary = new OrderSummary();
        summary.setId(order.getId());
        summary.setTotalAmount(order.getTotalAmount());
        summary.setOrderStatus(order.getOrderStatus());
        summary.setOrderDate(order.getOrderDate());
        
        // Map customer manually to avoid circular dependency
        if (order.getCustomer() != null) {
            CustomerSummary customerSummary = new CustomerSummary();
            customerSummary.setId(order.getCustomer().getId());
            customerSummary.setFullName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
            customerSummary.setEmail(order.getCustomer().getUser().getEmail());
            customerSummary.setMobile(order.getCustomer().getMobile());
            summary.setCustomer(customerSummary);
        }
        
        return summary;
    }
}
