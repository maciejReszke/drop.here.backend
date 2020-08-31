package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CustomerDataGenerator {

    public Customer customer(int i, Account account) {
        return Customer.builder()
                .firstName("firstCustomerName" + i)
                .lastName("lastCustomerName" + i)
                .account(account)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    public CustomerManagementRequest managementRequest(int i) {
        return CustomerManagementRequest.builder()
                .firstName("firstName" + i)
                .lastName("lastName" + i)
                .build();
    }
}
