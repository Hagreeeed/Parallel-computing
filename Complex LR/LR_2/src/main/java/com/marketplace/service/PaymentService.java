package com.marketplace.service;

import com.marketplace.dto.request.CreatePaymentRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.InvalidStatusTransitionException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Commission;
import com.marketplace.model.Order;
import com.marketplace.model.OrderItem;
import com.marketplace.model.Payment;
import com.marketplace.model.Seller;
import com.marketplace.model.enums.OrderStatus;
import com.marketplace.model.enums.PaymentStatus;
import com.marketplace.repository.CommissionRepository;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.PaymentRepository;
import com.marketplace.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CommissionRepository commissionRepository;
    private final SellerRepository sellerRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
            CommissionRepository commissionRepository, SellerRepository sellerRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.commissionRepository = commissionRepository;
        this.sellerRepository = sellerRepository;
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order " + orderId + " not found"));
    }

    public Payment create(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessRuleException("Payment requires CONFIRMED order");
        }

        paymentRepository.findByOrderId(request.getOrderId())
                .ifPresent(p -> {
                    throw new BusinessRuleException("Payment already exists for order " + request.getOrderId());
                });

        Payment payment = new Payment(request.getOrderId(), request.getMethod(), order.getFinalAmount());
        return paymentRepository.save(payment);
    }

    public Payment complete(Long id) {
        Payment payment = findById(id);
        validateTransition(payment.getStatus(), PaymentStatus.COMPLETED);

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Auto-create commissions for each seller in this order
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", payment.getOrderId()));

        Set<Long> processedSellers = new HashSet<>();
        for (OrderItem item : order.getItems()) {
            Long sellerId = item.getSellerId();
            if (!processedSellers.contains(sellerId)) {
                processedSellers.add(sellerId);

                double sellerTotal = order.getItems().stream()
                        .filter(i -> i.getSellerId().equals(sellerId))
                        .mapToDouble(OrderItem::getSubtotal)
                        .sum();

                Seller seller = sellerRepository.findById(sellerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));

                Commission commission = new Commission(
                        order.getId(),
                        sellerId,
                        sellerTotal,
                        seller.getCommissionRate());
                commissionRepository.save(commission);

                // Update totalSales
                seller.setTotalSales(seller.getTotalSales() + 1);
                sellerRepository.save(seller);
            }
        }

        return payment;
    }

    public Payment fail(Long id) {
        Payment payment = findById(id);
        validateTransition(payment.getStatus(), PaymentStatus.FAILED);
        payment.setStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    public Payment refund(Long id) {
        Payment payment = findById(id);
        validateTransition(payment.getStatus(), PaymentStatus.REFUNDED);
        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    private void validateTransition(PaymentStatus from, PaymentStatus to) {
        boolean valid = switch (to) {
            case PROCESSING -> from == PaymentStatus.PENDING;
            case COMPLETED -> from == PaymentStatus.PENDING || from == PaymentStatus.PROCESSING;
            case FAILED -> from == PaymentStatus.PROCESSING;
            case REFUNDED -> from == PaymentStatus.FAILED;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException("Payment", from.name(), to.name());
        }
    }
}
