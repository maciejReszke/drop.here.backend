package com.drop.here.backend.drophere.order.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.order.dto.OrderCustomerDecisionRequest;
import com.drop.here.backend.drophere.order.dto.OrderCustomerResponse;
import com.drop.here.backend.drophere.order.dto.OrderProcessingRequest;
import com.drop.here.backend.drophere.order.dto.OrderSubmissionRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import com.drop.here.backend.drophere.order.enums.OrderProcessOperation;
import com.drop.here.backend.drophere.order.enums.OrderStatus;
import com.drop.here.backend.drophere.order.service.processing_service.OrderProcessingServiceFactory;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderService {
    private final OrderValidationService orderValidationService;
    private final OrderMappingService orderMappingService;
    private final DropService dropService;
    private final OrderProcessingServiceFactory orderProcessingServiceFactory;
    private final OrderPersistenceService orderPersistenceService;
    private final OrderSearchingService orderSearchingService;


    // TODO: 12/10/2020  test
    public ResourceOperationResponse createOrder(String dropUid, OrderSubmissionRequest orderSubmissionRequest, AccountAuthentication authentication) {
        final Customer customer = authentication.getCustomer();
        final Drop drop = dropService.findPrivilegedDrop(dropUid, customer);
        orderValidationService.validateCreateOrderRequest(orderSubmissionRequest);
        final Order order = orderMappingService.toEntity(drop, orderSubmissionRequest, customer);
        final OrderStatus orderStatus = orderProcessingServiceFactory.process(order, OrderProcessingRequest.submission(orderSubmissionRequest), OrderProcessOperation.NEW);
        log.info("Created new order with status {} for customer {} drop {}", orderStatus, customer.getId(), drop.getUid());
        order.setOrderStatus(orderStatus);
        orderPersistenceService.save(order);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, order.getId());
    }


    public Page<OrderCustomerResponse> findCustomerOrders(AccountAuthentication authentication, String status, Pageable pageable) {
        return orderSearchingService.findCustomerOrders(authentication.getCustomer(), status, pageable);
    }

    // TODO: 13/10/2020  test
    public ResourceOperationResponse updateOrder(Long orderId, OrderSubmissionRequest orderSubmissionRequest, AccountAuthentication authentication) {
        final Order order = orderPersistenceService.findOrder(orderId, authentication.getCustomer());
        orderValidationService.validateUpdateOrderRequest(order, orderSubmissionRequest);
        orderMappingService.update(order, orderSubmissionRequest);
        final OrderStatus orderStatus = orderProcessingServiceFactory.process(order, OrderProcessingRequest.submission(orderSubmissionRequest), OrderProcessOperation.BY_CUSTOMER_UPDATED);
        return updateOrder(authentication, order, orderStatus);
    }

    // TODO: 13/10/2020 test
    public ResourceOperationResponse updateOrderStatus(Long orderId, OrderCustomerDecisionRequest orderCustomerDecisionRequest, AccountAuthentication authentication) {
        final Order order = orderPersistenceService.findOrder(orderId, authentication.getCustomer());
        final OrderStatus orderStatus = orderProcessingServiceFactory.process(order, OrderProcessingRequest.decision(orderCustomerDecisionRequest), OrderProcessOperation.CUSTOMER_DECISION);
        return updateOrder(authentication, order, orderStatus);
    }

    private ResourceOperationResponse updateOrder(AccountAuthentication authentication, Order order, OrderStatus orderStatus) {
        log.info("Updated order with status {} to {} for customer {} order {}", order.getOrderStatus(), orderStatus, authentication.getCustomer().getId(), order.getId());
        order.setOrderStatus(orderStatus);
        orderPersistenceService.save(order);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, order.getId());
    }
}
