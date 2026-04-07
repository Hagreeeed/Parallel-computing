package com.marketplace.repository;

import com.marketplace.model.Commission;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CommissionRepository {

    private final Map<Long, Commission> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Commission save(Commission commission) {
        if (commission.getId() == null) {
            commission.setId(idGenerator.getAndIncrement());
        }
        store.put(commission.getId(), commission);
        return commission;
    }

    public Optional<Commission> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Commission> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Commission> findBySellerId(Long sellerId) {
        return store.values().stream()
                .filter(c -> c.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
