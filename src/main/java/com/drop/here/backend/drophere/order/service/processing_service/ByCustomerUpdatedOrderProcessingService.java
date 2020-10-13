package com.drop.here.backend.drophere.order.service.processing_service;

import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ByCustomerUpdatedOrderProcessingService implements OrderProcessingService {

    // TODO: 13/10/2020 dwie opcje - palced lub compromised
    @Override
    public OrderStatus process(Order order, OrderProcessingRequest submission) {
        return null;
    }
}
