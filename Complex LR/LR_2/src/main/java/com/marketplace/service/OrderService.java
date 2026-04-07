package com.marketplace.service;

import com.marketplace.dto.request.CreateOrderItemRequest;
import com.marketplace.dto.request.CreateOrderRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.InvalidStatusTransitionException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Order;
import com.marketplace.model.OrderItem;
import com.marketplace.model.Product;
import com.marketplace.model.enums.OrderStatus;
import com.marketplace.model.enums.ProductStatus;
import com.marketplace.repository.CustomerRepository;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    public List<Order> findByCustomerId(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        return orderRepository.findByCustomerId(customerId);
    }

    public Order create(CreateOrderRequest request) {
        customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.getProductId()));

            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new BusinessRuleException("Product " + product.getId() + " is not available");
            }

            if (product.getStock() < itemReq.getQuantity()) {
                throw new BusinessRuleException("Insufficient stock for product " + product.getId());
            }

            // Create snapshot
            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getSellerId(),
                    product.getName(),
                    product.getPrice(),
                    itemReq.getQuantity());
            orderItems.add(orderItem);
        }

        // Calculate amounts
        double totalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();

        double discountAmount = 0.0;
        if (totalAmount > 5000) {
            discountAmount = totalAmount * 0.05;
        }

        double finalAmount = totalAmount - discountAmount;

        Order order = new Order(request.getCustomerId(), orderItems, totalAmount, discountAmount, finalAmount);
        return orderRepository.save(order);
    }

    public Order confirm(Long id) {
        Order order = findById(id);
        validateTransition(order.getStatus(), OrderStatus.CONFIRMED);
        order.setStatus(OrderStatus.CONFIRMED);

        // Decrease stock on CONFIRMED
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new BusinessRuleException("Insufficient stock for product " + product.getId());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }

    public Order ship(Long id) {
        Order order = findById(id);
        validateTransition(order.getStatus(), OrderStatus.SHIPPED);
        order.setStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    public Order deliver(Long id) {
        Order order = findById(id);
        validateTransition(order.getStatus(), OrderStatus.DELIVERED);
        order.setStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    public Order cancel(Long id) {
        Order order = findById(id);
        validateTransition(order.getStatus(), OrderStatus.CANCELLED);

        // Restore stock on CANCEL
        for (OrderItem item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                if (product.getStatus() == ProductStatus.OUT_OF_STOCK && product.getStock() > 0) {
                    product.setStatus(ProductStatus.ACTIVE);
                }
                productRepository.save(product);
            });
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    private void validateTransition(OrderStatus from, OrderStatus to) {
        boolean valid = switch (to) {
            case CONFIRMED -> from == OrderStatus.PENDING;
            case SHIPPED -> from == OrderStatus.CONFIRMED;
            case DELIVERED -> from == OrderStatus.SHIPPED;
            case CANCELLED -> from == OrderStatus.PENDING || from == OrderStatus.CONFIRMED;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException("Order", from.name(), to.name());
        }
    }
}
