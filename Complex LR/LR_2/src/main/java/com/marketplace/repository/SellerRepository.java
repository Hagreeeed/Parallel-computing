package com.marketplace.repository;

import com.marketplace.model.Seller;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class SellerRepository {

    private final Map<Long, Seller> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Seller save(Seller seller) {
        if (seller.getId() == null) {
            seller.setId(idGenerator.getAndIncrement());
        }
        store.put(seller.getId(), seller);
        return seller;
    }

    public Optional<Seller> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Seller> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Seller> findByUserId(Long userId) {
        return store.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
