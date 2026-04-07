package com.marketplace.service;

import com.marketplace.dto.request.CreateCustomerRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Customer;
import com.marketplace.model.User;
import com.marketplace.model.enums.UserRole;
import com.marketplace.repository.CustomerRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    public Customer create(CreateCustomerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new BusinessRuleException("User role must be CUSTOMER");
        }

        customerRepository.findByUserId(request.getUserId())
                .ifPresent(c -> {
                    throw new BusinessRuleException("Customer profile already exists for user " + request.getUserId());
                });

        Customer customer = new Customer(
                request.getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getShippingAddress());
        return customerRepository.save(customer);
    }
}
