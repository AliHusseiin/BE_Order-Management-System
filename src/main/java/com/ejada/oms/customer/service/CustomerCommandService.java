package com.ejada.oms.customer.service;

import com.ejada.oms.customer.dto.req.AddressCreateRequest;
import com.ejada.oms.customer.dto.req.CustomerCreateRequest;
import com.ejada.oms.customer.entity.Address;
import com.ejada.oms.customer.entity.Customer;
import com.ejada.oms.customer.repository.CustomerRepository;
import com.ejada.oms.auth.entity.User;
import com.ejada.oms.auth.service.UserService;
import com.ejada.oms.core.exception.BusinessException;
import com.ejada.oms.core.exception.DataIntegrityException;
import com.ejada.oms.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerCommandService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;

    public Customer create(CustomerCreateRequest request) {
        log.info("Creating customer: {}", request.getEmail());

        if (customerRepository.existsByUserEmail(request.getEmail())) {
            throw BusinessException.emailAlreadyExists(request.getEmail());
        }

        if (customerRepository.existsByMobile(request.getMobile())) {
            throw BusinessException.mobileAlreadyExists(request.getMobile());
        }

        User user = userService.createCustomerUser(request.getUsername(), request.getEmail(), request.getPassword());

        Customer customer = Customer.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mobile(request.getMobile())
                .build();

        // Add addresses if present
        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            for (AddressCreateRequest addrReq : request.getAddresses()) {
                Address address = Address.builder()
                        .addressType(addrReq.getAddressType())
                        .streetAddress(addrReq.getStreetAddress())
                        .city(addrReq.getCity())
                        .state(addrReq.getState())
                        .postalCode(addrReq.getPostalCode())
                        .country(addrReq.getCountry())
                        .isDefault(addrReq.isDefault())
                        .build();

                // Maintain bidirectional relationship
                customer.addAddress(address);
            }
        }

        Customer saved = customerRepository.save(customer);
        log.info("Customer created with ID: {} and {} addresses",
                saved.getId(), saved.getAddresses().size());
        return saved;
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> BusinessException.customerNotFound(id));
    }

    public void delete(Long id) {
        Customer customer = findById(id);
        
        // Check if customer has any orders before deletion
        if (orderRepository.existsByCustomerId(id)) {
            throw DataIntegrityException.customerHasOrders(id);
        }
        
        log.info("Deleting customer: {}", id);
        customerRepository.delete(customer);
        log.info("Customer deleted: {}", id);
    }
}