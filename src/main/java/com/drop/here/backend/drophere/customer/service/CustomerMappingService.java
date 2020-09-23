package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// TODO MONO:
@Service
public class CustomerMappingService {

    public Customer toCustomer(Account account, ExternalAuthenticationResult result) {
        return Customer.builder()
                .account(account)
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    public Customer createCustomer(CustomerManagementRequest customerManagementRequest, Account principal) {
        final Customer customer = Customer.builder()
                .account(principal)
                .createdAt(LocalDateTime.now())
                .build();
        updateCustomer(customerManagementRequest, customer);
        return customer;
    }

    public void updateCustomer(CustomerManagementRequest customerManagementRequest, Customer customer) {
        customer.setFirstName(customerManagementRequest.getFirstName().trim());
        customer.setLastName(customerManagementRequest.getLastName().trim());
        customer.setLastUpdatedAt(LocalDateTime.now());
    }

    public CustomerManagementResponse toManagementResponse(Customer customer) {
        return customer == null
                ? CustomerManagementResponse.builder().registered(false).build()
                : CustomerManagementResponse.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .registered(true)
                .build();
    }
}
