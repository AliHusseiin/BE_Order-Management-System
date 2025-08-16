package com.ejada.oms.customer.service;

import com.ejada.oms.customer.entity.Customer;
import com.ejada.oms.customer.repository.CustomerRepository;
import com.ejada.oms.core.service.BaseListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerQueryService extends BaseListingService<Customer, Long> {

    private final CustomerRepository customerRepository;

    @Override
    protected JpaRepository<Customer, Long> getRepository() {
        return customerRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Customer> getSpecificationRepository() {
        return customerRepository;
    }

    @Override
    protected Class<Customer> getEntityClass() {
        return Customer.class;
    }

}