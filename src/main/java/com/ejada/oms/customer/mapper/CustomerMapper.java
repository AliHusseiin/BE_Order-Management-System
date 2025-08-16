package com.ejada.oms.customer.mapper;

import com.ejada.oms.customer.dto.res.CustomerResponse;
import com.ejada.oms.customer.dto.res.CustomerSummary;
import com.ejada.oms.customer.entity.Customer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    CustomerResponse toResponse(Customer customer);

    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    CustomerSummary toSummary(Customer customer);
}