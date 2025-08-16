package com.ejada.oms.customer.repository;

import com.ejada.oms.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Customer entity operations.
 * 
 * @author Ali Hussein
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByUserEmail(String email);
    boolean existsByUserEmail(String email);
    boolean existsByMobile(String mobile);
}