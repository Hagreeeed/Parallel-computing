package com.marketplace.auth.service;

import com.marketplace.auth.dto.request.BecomeSellerRequest;
import com.marketplace.auth.dto.request.CreateUserRequest;
import com.marketplace.auth.dto.request.RegisterUserRequest;
import com.marketplace.auth.exception.BusinessRuleException;
import com.marketplace.auth.exception.ResourceNotFoundException;
import com.marketplace.auth.model.Customer;
import com.marketplace.auth.model.Seller;
import com.marketplace.auth.model.User;
import com.marketplace.auth.model.enums.UserRole;
import com.marketplace.auth.repository.CustomerRepository;
import com.marketplace.auth.repository.SellerRepository;
import com.marketplace.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CustomerRepository customerRepository, SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository; this.passwordEncoder = passwordEncoder;
    }

    public Page<User> findAll(Pageable pageable) { return userRepository.findAll(pageable); }
    public User findById(Long id) { return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)); }

    @Transactional
    public User register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new BusinessRuleException("User with email " + request.getEmail() + " already exists");
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), UserRole.CUSTOMER);
        user = userRepository.save(user);
        Customer customer = new Customer(user,
                request.getFirstName() != null ? request.getFirstName() : "",
                request.getLastName() != null ? request.getLastName() : "",
                request.getPhone() != null ? request.getPhone() : "",
                request.getShippingAddress() != null ? request.getShippingAddress() : "");
        customerRepository.save(customer);
        return user;
    }

    @Transactional
    public Seller becomeSeller(Long userId, BecomeSellerRequest request) {
        User user = findById(userId);
        if (sellerRepository.existsByUser_Id(userId)) throw new BusinessRuleException("User " + userId + " is already a Seller");
        Double commission = request.getCommissionRate() != null ? request.getCommissionRate() : 0.05;
        Seller seller = new Seller(user, request.getShopName(), request.getDescription(), commission);
        return sellerRepository.save(seller);
    }

    @Transactional
    public User update(Long id, CreateUserRequest request) {
        User user = findById(id);
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail()))
            throw new BusinessRuleException("User with email " + request.getEmail() + " already exists");
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        customerRepository.findByUser_Id(id).ifPresent(customerRepository::delete);
        sellerRepository.findByUser_Id(id).ifPresent(sellerRepository::delete);
        userRepository.delete(user);
    }
}
