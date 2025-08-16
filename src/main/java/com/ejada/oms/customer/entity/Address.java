package com.ejada.oms.customer.entity;

import com.ejada.oms.core.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Address entity representing customer addresses.
 * 
 * @author Ali Hussein
 */
@Entity
@Table(name = "address")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "address_id"))
public class Address extends AuditableEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank
    @Column(name = "address_type", nullable = false, length = 50)
    private String addressType;

    @NotBlank
    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @NotBlank
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @NotBlank
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @NotBlank
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

}