package com.marketplace.repository;

import com.marketplace.model.Category;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CategoryRepository {

    private final Map<Long, Category> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idGenerator.getAndIncrement());
        }
        store.put(category.getId(), category);
        return category;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Category> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Category> findByParentId(Long parentId) {
        return store.values().stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
