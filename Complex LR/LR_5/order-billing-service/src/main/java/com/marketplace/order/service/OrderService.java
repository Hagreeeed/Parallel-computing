package com.marketplace.order.service;

import com.marketplace.dto.ProductDTO;
import com.marketplace.order.dto.request.CreateOrderItemRequest;
import com.marketplace.order.dto.request.CreateOrderRequest;
import com.marketplace.order.exception.BusinessRuleException;
import com.marketplace.order.exception.InvalidStatusTransitionException;
import com.marketplace.order.exception.ResourceNotFoundException;
import com.marketplace.order.feign.CatalogClient;
import com.marketplace.order.model.Order;
import com.marketplace.order.model.OrderItem;
import com.marketplace.order.model.enums.OrderStatus;
import com.marketplace.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    public Page<Order> findAll(Pageable pageable) { return orderRepository.findAll(pageable); }
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) { return orderRepository.findByCustomerId(customerId, pageable); }
    public Order findById(Long id) { return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id)); }

    @Transactional
    public Order create(CreateOrderRequest request) {
        Order order = new Order(request.getCustomerId());
        
        for (CreateOrderItemRequest itemReq : request.getItems()) {
            ProductDTO p = catalogClient.getProductById(itemReq.getProductId());
            if (p.getId() == null || "Service unavailable".equals(p.getName())) {
                throw new BusinessRuleException("Cannot verify product " + itemReq.getProductId() + ", catalog service may be unavailable");
            }
            if (p.getStock() < itemReq.getQuantity()) {
                throw new BusinessRuleException("Not enough stock for product " + p.getId() + ". Available: " + p.getStock());
            }
            
            OrderItem item = new OrderItem(p.getId(), p.getSellerId(), itemReq.getQuantity(), p.getPrice());
            order.addItem(item);
            
            // Decrease stock in catalog service
            catalogClient.decreaseStock(p.getId(), itemReq.getQuantity());
        }
        
        return orderRepository.save(order);
    }

    @Transactional
    public Order ship(Long id) {
        Order order = findById(id);
        if (order.getStatus() != OrderStatus.PAID) throw new InvalidStatusTransitionException("Order", order.getStatus().name(), OrderStatus.SHIPPED.name());
        order.setStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order deliver(Long id) {
        Order order = findById(id);
        if (order.getStatus() != OrderStatus.SHIPPED) throw new InvalidStatusTransitionException("Order", order.getStatus().name(), OrderStatus.DELIVERED.name());
        order.setStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancel(Long id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) 
            throw new InvalidStatusTransitionException("Order", order.getStatus().name(), OrderStatus.CANCELLED.name());
            
        if (order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            for (OrderItem item : order.getItems()) {
                try {
                    catalogClient.increaseStock(item.getProductId(), item.getQuantity());
                } catch(Exception ignored) {}
            }
        }
        return orderRepository.save(order);
    }
}
