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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<Seller> findAll(Pageable pageable) {
        return sellerRepository.findAll(pageable);
    }

    public Seller findById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", id));
    }

    public Page<Product> findProductsBySellerId(Long sellerId, Pageable pageable) {
        findById(sellerId); // verify seller exists
        return productRepository.findBySeller_Id(sellerId, pageable);
    }

    @Transactional
    public Seller create(CreateSellerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (user.getRole() != UserRole.SELLER) {
            throw new BusinessRuleException("User role must be SELLER");
        }

        if (sellerRepository.existsByUser_Id(request.getUserId())) {
            throw new BusinessRuleException("Seller profile already exists for user " + request.getUserId());
        }

        Double commissionRate = request.getCommissionRate() != null ? request.getCommissionRate() : 0.05;

        Seller seller = new Seller(
                user,
                request.getShopName(),
                request.getDescription(),
                commissionRate);
        return sellerRepository.save(seller);
    }

    @Transactional
    public Seller verifySeller(Long id) {
        Seller seller = findById(id);
        if (seller.getVerified()) {
            throw new BusinessRuleException("Seller is already verified");
        }
        seller.setVerified(true);
        
        User user = seller.getUser();
        user.setRole(UserRole.SELLER);
        userRepository.save(user);

        return sellerRepository.save(seller);
    }

    @Transactional
    public Seller update(Long id, CreateSellerRequest request) {
        Seller seller = findById(id);
        seller.setShopName(request.getShopName());
        seller.setDescription(request.getDescription());
        if (request.getCommissionRate() != null) {
            seller.setCommissionRate(request.getCommissionRate());
        }
        return sellerRepository.save(seller);
    }

    @Transactional
    public void delete(Long id) {
        Seller seller = findById(id);
        sellerRepository.delete(seller);
    }
}
