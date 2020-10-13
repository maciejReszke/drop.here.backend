package com.drop.here.backend.drophere.order.service.processing_service;

import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderStatus;

public interface OrderProcessingService {
    OrderStatus process(Order order, OrderProcessingRequest submission);
}
