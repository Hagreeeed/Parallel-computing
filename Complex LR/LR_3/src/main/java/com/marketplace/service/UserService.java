package com.marketplace.service;

import com.marketplace.dto.request.BecomeSellerRequest;
import com.marketplace.dto.request.CreateUserRequest;
import com.marketplace.dto.request.RegisterUserRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Customer;
import com.marketplace.model.Seller;
import com.marketplace.model.User;
import com.marketplace.model.enums.UserRole;
import com.marketplace.repository.CustomerRepository;
import com.marketplace.repository.SellerRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CustomerRepository customerRepository, SellerRepository sellerRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public User register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("User with email " + request.getEmail() + " already exists");
        }
        
        // 1. Створюємо базового юзера (по замовчуванню CUSTOMER)
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), UserRole.CUSTOMER);
        user = userRepository.save(user);

        // 2. Створюємо профіль покупця і підв'язуємо до цього юзера
        Customer customer = new Customer(
                user,
                request.getFirstName() != null ? request.getFirstName() : "",
                request.getLastName() != null ? request.getLastName() : "",
                request.getPhone() != null ? request.getPhone() : "",
                request.getShippingAddress() != null ? request.getShippingAddress() : ""
        );
        customerRepository.save(customer);

        return user;
    }

    @Transactional
    public Seller becomeSeller(Long userId, BecomeSellerRequest request) {
        User user = findById(userId);

        if (sellerRepository.existsByUser_Id(userId)) {
            throw new BusinessRuleException("User " + userId + " is already a Seller");
        }

        // Профіль створюється зі статусом verified = false (з конструктора Seller).
        // Роль юзера залишається CUSTOMER доки Адмін не підтвердить заявку.

        Double commission = request.getCommissionRate() != null ? request.getCommissionRate() : 0.05;

        // Створюємо профіль продавця
        Seller seller = new Seller(
                user,
                request.getShopName(),
                request.getDescription(),
                commission
        );
        
        return sellerRepository.save(seller);
    }

    @Transactional
    public User update(Long id, CreateUserRequest request) {
        User user = findById(id);
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("User with email " + request.getEmail() + " already exists");
        }
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        
        // Видалення каскадом якщо є профілі:
        customerRepository.findByUser_Id(id).ifPresent(customerRepository::delete);
        sellerRepository.findByUser_Id(id).ifPresent(sellerRepository::delete);
        
        userRepository.delete(user);
    }
}
