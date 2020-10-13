package com.drop.here.backend.drophere.order.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderPersistenceService {
    private final OrderRepository orderRepository;

    public void save(Order order) {
        orderRepository.save(order);
    }

    // TODO: 13/10/2020
    public Order findOrder(Long orderId, Customer customer) {
        return null;
    }
}
