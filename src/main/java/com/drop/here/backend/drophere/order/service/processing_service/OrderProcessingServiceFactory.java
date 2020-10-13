package com.drop.here.backend.drophere.order.service.processing_service;

import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderProcessOperation;
import com.drop.here.backend.drophere.order.enums.OrderStatus;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class OrderProcessingServiceFactory {
    private final NewOrderProcessingService newOrderProcessingService;
    private final ByCustomerUpdatedOrderProcessingService byCustomerUpdatedOrderProcessingService;
    private final CustomerDecisionOrderProcessingServiceFactory customerDecisionOrderProcessingServiceFactory;
    private final CompanyDecisionOrderProcessingServiceFactory companyDecisionOrderProcessingServiceFactory;
    private final ByCompanyUpdatedOrderProcessingServiceFactory byCompanyUpdatedOrderProcessingServiceFactory;

    // TODO: 13/10/2020 test, implement
    public OrderStatus process(Order order, OrderProcessingRequest request, OrderProcessOperation orderProcessOperation) {
        return API.Match(orderProcessOperation).of(
                Case($(OrderProcessOperation.NEW), () -> newOrderProcessingService.process(order, request)),
                Case($(OrderProcessOperation.BY_CUSTOMER_UPDATED), () -> byCustomerUpdatedOrderProcessingService.process(order, request)),
                Case($(OrderProcessOperation.CUSTOMER_DECISION), () -> customerDecisionOrderProcessingServiceFactory.process(order, request)),
                Case($(OrderProcessOperation.BY_COMPANY_UPDATED), () -> byCompanyUpdatedOrderProcessingServiceFactory.process(order, request)),
                Case($(OrderProcessOperation.COMPANY_DECISION), () -> companyDecisionOrderProcessingServiceFactory.process(order, request))
        );
    }
}
