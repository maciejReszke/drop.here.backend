package com.drop.here.backend.drophere.order.service.processing_service;

import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerDecisionOrderProcessingServiceFactory implements OrderProcessingService {
    private final CancelCustomerDecisionOrderProcessingService cancelCustomerDecisionOrderProcessingService;
    private final AcceptCustomerDecisionOrderProcessingService acceptCustomerDecisionOrderProcessingService;

    // TODO: 13/10/2020 test, implement
    @Override
    public OrderStatus process(Order order, OrderProcessingRequest request) {
        return null;
    }
}
