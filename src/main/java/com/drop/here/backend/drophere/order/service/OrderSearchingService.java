package com.drop.here.backend.drophere.order.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.order.dto.OrderCustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderSearchingService {

    // TODO: 13/10/2020
    public Page<OrderCustomerResponse> findCustomerOrders(Customer customer, String status, Pageable pageable) {
        return null;
    }
}
