package com.ejada.oms.customer.dto.res;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobile;
    private LocalDateTime createdAt;
}

