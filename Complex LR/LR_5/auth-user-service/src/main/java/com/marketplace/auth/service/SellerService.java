package com.marketplace.auth.service;

import com.marketplace.auth.dto.request.CreateSellerRequest;
import com.marketplace.auth.exception.BusinessRuleException;
import com.marketplace.auth.exception.ResourceNotFoundException;
import com.marketplace.auth.model.Seller;
import com.marketplace.auth.model.User;
import com.marketplace.auth.model.enums.UserRole;
import com.marketplace.auth.repository.SellerRepository;
import com.marketplace.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public SellerService(SellerRepository sellerRepository, UserRepository userRepository) {
        this.sellerRepository = sellerRepository; this.userRepository = userRepository;
    }

    public Page<Seller> findAll(Pageable pageable) { return sellerRepository.findAll(pageable); }
    public Seller findById(Long id) { return sellerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seller", id)); }

    @Transactional
    public Seller verifySeller(Long id) {
        Seller seller = findById(id);
        if (seller.getVerified()) throw new BusinessRuleException("Seller is already verified");
        seller.setVerified(true);
        User user = seller.getUser();
        user.setRole(UserRole.SELLER);
        userRepository.save(user);
        return sellerRepository.save(seller);
    }

    @Transactional
    public Seller update(Long id, CreateSellerRequest request) {
        Seller s = findById(id);
        s.setShopName(request.getShopName()); s.setDescription(request.getDescription());
        if (request.getCommissionRate() != null) s.setCommissionRate(request.getCommissionRate());
        return sellerRepository.save(s);
    }

    @Transactional
    public void delete(Long id) { sellerRepository.delete(findById(id)); }
}
