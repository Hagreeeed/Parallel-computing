package com.marketplace.service;

import com.marketplace.dto.request.CreateSellerRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Product;
import com.marketplace.model.Seller;
import com.marketplace.model.User;
import com.marketplace.model.enums.UserRole;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.SellerRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public SellerService(SellerRepository sellerRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    public Seller findById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", id));
    }

    public List<Product> findProductsBySellerId(Long sellerId) {
        findById(sellerId); // verify seller exists
        return productRepository.findBySellerId(sellerId);
    }

    public Seller create(CreateSellerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (user.getRole() != UserRole.SELLER) {
            throw new BusinessRuleException("User role must be SELLER");
        }

        sellerRepository.findByUserId(request.getUserId())
                .ifPresent(s -> {
                    throw new BusinessRuleException("Seller profile already exists for user " + request.getUserId());
                });

        Double commissionRate = request.getCommissionRate() != null ? request.getCommissionRate() : 0.05;

        Seller seller = new Seller(
                request.getUserId(),
                request.getShopName(),
                request.getDescription(),
                commissionRate);
        return sellerRepository.save(seller);
    }
}
