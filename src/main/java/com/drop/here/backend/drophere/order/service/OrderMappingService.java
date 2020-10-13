package com.drop.here.backend.drophere.order.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.order.dto.OrderSubmissionRequest;
import com.drop.here.backend.drophere.order.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderMappingService {

    // TODO: 13/10/2020 test, implement
    public Order toEntity(Drop drop, OrderSubmissionRequest orderSubmissionRequest, Customer customer) {

        return null;
    }

    // TODO: 13/10/2020  test, impppemetn

    public void update(Order order, OrderSubmissionRequest orderSubmissionRequest) {

    }
}
