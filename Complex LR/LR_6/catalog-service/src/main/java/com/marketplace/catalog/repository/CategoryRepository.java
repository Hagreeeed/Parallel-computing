package com.marketplace.catalog.repository;
import com.marketplace.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<Category, Long> { List<Category> findByParent_Id(Long parentId); }
