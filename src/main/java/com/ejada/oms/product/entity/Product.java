package com.ejada.oms.product.entity;

import com.ejada.oms.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product entity representing items available for purchase.
 * 
 * @author Ali Hussein
 */
@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseEntity<Long> {

    @NotBlank
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(value = 0)
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "category", length = 100)
    private String category;

    public boolean hasStock(Integer requiredQuantity) {
        return stockQuantity >= requiredQuantity;
    }

    public void reduceStock(Integer quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalStateException("Insufficient stock for product: " + productName);
        }
        this.stockQuantity -= quantity;
    }
}
