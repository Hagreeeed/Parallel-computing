package com.marketplace.repository;

import com.marketplace.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PaymentRepository {

    private final Map<Long, Payment> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idGenerator.getAndIncrement());
        }
        store.put(payment.getId(), payment);
        return payment;
    }

    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Payment> findByOrderId(Long orderId) {
        return store.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
