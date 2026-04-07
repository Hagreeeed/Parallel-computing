package com.marketplace.repository;

import com.marketplace.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerRepository {

    private final Map<Long, Customer> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        store.put(customer.getId(), customer);
        return customer;
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Customer> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Customer> findByUserId(Long userId) {
        return store.values().stream()
                .filter(c -> c.getUserId().equals(userId))
                .findFirst();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
