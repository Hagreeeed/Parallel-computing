package com.marketplace.repository;

import com.marketplace.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        store.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Product> findBySellerId(Long sellerId) {
        return store.values().stream()
                .filter(p -> p.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return store.values().stream()
                .filter(p -> p.getCategoryIds().contains(categoryId))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
