package com.drop.here.backend.drophere.order.service.processing_service;

import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class RejectCompanyDecisionOrderProcessingService implements OrderProcessingService{

    // TODO: 13/10/2020 test, implement
    @Override
    public OrderStatus process(Order order, OrderProcessingRequest submission) {
        return null;
    }
}
