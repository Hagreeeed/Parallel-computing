package com.marketplace.config;

import com.marketplace.model.Customer;
import com.marketplace.model.User;
import com.marketplace.model.enums.UserRole;
import com.marketplace.repository.CustomerRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        String adminEmail = "admin@marketplace.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(adminEmail, passwordEncoder.encode("password"), UserRole.ADMIN);
            admin = userRepository.save(admin);

            Customer customer = new Customer(admin, "System", "Admin", "", "");
            customerRepository.save(customer);
            
            System.out.println("Default Admin created: " + adminEmail + " / password");
        }
    }
}
