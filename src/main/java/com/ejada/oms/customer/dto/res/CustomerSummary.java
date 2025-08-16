package com.ejada.oms.customer.dto.res;

import lombok.Data;

@Data
public class CustomerSummary {
    private Long id;
    private String fullName;
    private String email;
    private String mobile;
}