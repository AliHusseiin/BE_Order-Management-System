package com.ejada.oms.order.dto.res;

import com.ejada.oms.customer.dto.res.CustomerSummary;
import com.ejada.oms.invoice.dto.res.InvoiceSummary;
import com.ejada.oms.order.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private CustomerSummary customer;
    private String createdByUsername;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> orderItems;
    private InvoiceSummary invoice;
}
