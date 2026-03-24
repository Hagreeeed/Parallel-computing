package com.marketplace.service;

import com.marketplace.dto.request.CreateCustomerRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Customer;
import com.marketplace.model.User;
import com.marketplace.model.enums.UserRole;
import com.marketplace.repository.CustomerRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Transactional
    public Customer create(CreateCustomerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new BusinessRuleException("User role must be CUSTOMER");
        }

        if (customerRepository.existsByUser_Id(request.getUserId())) {
            throw new BusinessRuleException("Customer profile already exists for user " + request.getUserId());
        }

        Customer customer = new Customer(
                user,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getShippingAddress());
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer update(Long id, CreateCustomerRequest request) {
        Customer customer = findById(id);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setShippingAddress(request.getShippingAddress());
        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }
}
