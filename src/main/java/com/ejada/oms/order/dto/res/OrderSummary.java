package com.ejada.oms.order.dto.res;

import com.ejada.oms.customer.dto.res.CustomerSummary;
import com.ejada.oms.order.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderSummary {
    private Long id;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private CustomerSummary customer;
}