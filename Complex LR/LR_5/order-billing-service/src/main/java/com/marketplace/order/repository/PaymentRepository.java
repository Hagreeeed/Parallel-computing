package com.marketplace.order.repository;

import com.marketplace.order.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_Id(Long orderId);
    boolean existsByOrder_Id(Long orderId);
}
