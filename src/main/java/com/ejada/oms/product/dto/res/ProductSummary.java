package com.ejada.oms.product.dto.res;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSummary {
    private Long id;
    private String productName;
    private String category;
    private BigDecimal price;
}
