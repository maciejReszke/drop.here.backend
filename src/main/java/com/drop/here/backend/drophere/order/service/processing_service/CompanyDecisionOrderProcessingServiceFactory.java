package com.drop.here.backend.drophere.order.service.processing_service;

import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyDecisionOrderProcessingServiceFactory implements OrderProcessingService {
    private final AcceptCompanyDecisionOrderProcessingService acceptCompanyDecisionOrderProcessingService;
    private final RejectCompanyDecisionOrderProcessingService rejectCompanyDecisionOrderProcessingService;
    private final CancelCompanyDecisionOrderProcessingService cancelCompanyDecisionOrderProcessingService;

    // TODO: 13/10/2020 test, implement
    @Override
    public OrderStatus process(Order order, OrderProcessingRequest submission) {
        return null;
    }
}
