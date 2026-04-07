package com.marketplace.order.service;

import com.marketplace.dto.SellerDTO;
import com.marketplace.order.dto.request.CreatePaymentRequest;
import com.marketplace.order.exception.BusinessRuleException;
import com.marketplace.order.exception.InvalidStatusTransitionException;
import com.marketplace.order.exception.ResourceNotFoundException;
import com.marketplace.order.feign.AuthUserClient;
import com.marketplace.order.model.Commission;
import com.marketplace.order.model.Order;
import com.marketplace.order.model.Payment;
import com.marketplace.order.model.enums.OrderStatus;
import com.marketplace.order.model.enums.PaymentStatus;
import com.marketplace.order.repository.CommissionRepository;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CommissionRepository commissionRepository;
    private final AuthUserClient authUserClient;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, CommissionRepository commissionRepository, AuthUserClient authUserClient) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.commissionRepository = commissionRepository;
        this.authUserClient = authUserClient;
    }

    public Page<Payment> findAll(Pageable pageable) { return paymentRepository.findAll(pageable); }
    public Payment findById(Long id) { return paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment", id)); }

    @Transactional
    public Payment process(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));
        if (order.getStatus() != OrderStatus.CREATED) throw new BusinessRuleException("Can only pay for CREATED orders");
        if (paymentRepository.existsByOrder_Id(order.getId())) throw new BusinessRuleException("Payment already initiated for order " + order.getId());

        Payment payment = new Payment(order, request.getMethod());
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment complete(Long id) {
        Payment payment = findById(id);
        if (payment.getStatus() != PaymentStatus.PENDING) throw new InvalidStatusTransitionException("Payment", payment.getStatus().name(), PaymentStatus.COMPLETED.name());
        
        payment.setStatus(PaymentStatus.COMPLETED);
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Calculate commissions grouped by seller
        Map<Long, Double> sellerTotals = order.getItems().stream().collect(Collectors.groupingBy(com.marketplace.order.model.OrderItem::getSellerId, Collectors.summingDouble(com.marketplace.order.model.OrderItem::getSubtotal)));

        for (Map.Entry<Long, Double> entry : sellerTotals.entrySet()) {
            Long sellerId = entry.getKey();
            Double subtotal = entry.getValue();
            SellerDTO s = authUserClient.getSellerById(sellerId);
            Double rate = s.getCommissionRate() != null ? s.getCommissionRate() : 0.05;
            Double commissionAmount = subtotal * rate;
            Commission commission = new Commission(order, sellerId, commissionAmount, rate);
            commissionRepository.save(commission);
            
            // Increment seller sales across service boundary
            try { authUserClient.incrementSales(sellerId); } catch (Exception ignored) {}
        }
        return paymentRepository.save(payment);
    }
}
