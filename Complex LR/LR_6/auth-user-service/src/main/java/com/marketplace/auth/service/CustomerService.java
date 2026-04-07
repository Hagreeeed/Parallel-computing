package com.marketplace.auth.service;

import com.marketplace.auth.dto.request.CreateCustomerRequest;
import com.marketplace.auth.exception.BusinessRuleException;
import com.marketplace.auth.exception.ResourceNotFoundException;
import com.marketplace.auth.model.Customer;
import com.marketplace.auth.model.User;
import com.marketplace.auth.model.enums.UserRole;
import com.marketplace.auth.repository.CustomerRepository;
import com.marketplace.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository; this.userRepository = userRepository;
    }

    public Page<Customer> findAll(Pageable pageable) { return customerRepository.findAll(pageable); }
    public Customer findById(Long id) { return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer", id)); }

    @Transactional
    public Customer create(CreateCustomerRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        if (user.getRole() != UserRole.CUSTOMER) throw new BusinessRuleException("User role must be CUSTOMER");
        if (customerRepository.existsByUser_Id(request.getUserId())) throw new BusinessRuleException("Customer profile already exists for user " + request.getUserId());
        return customerRepository.save(new Customer(user, request.getFirstName(), request.getLastName(), request.getPhone(), request.getShippingAddress()));
    }

    @Transactional
    public Customer update(Long id, CreateCustomerRequest request) {
        Customer c = findById(id);
        c.setFirstName(request.getFirstName()); c.setLastName(request.getLastName());
        c.setPhone(request.getPhone()); c.setShippingAddress(request.getShippingAddress());
        return customerRepository.save(c);
    }

    @Transactional
    public void delete(Long id) { customerRepository.delete(findById(id)); }
}
