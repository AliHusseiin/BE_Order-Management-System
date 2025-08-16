package com.ejada.oms.order.dto.res;

import com.ejada.oms.product.dto.res.ProductSummary;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private ProductSummary product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}

