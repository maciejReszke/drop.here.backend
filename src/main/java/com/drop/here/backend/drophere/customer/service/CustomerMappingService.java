package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerMappingService {

    public Customer toCustomer(Account account, ExternalAuthenticationResult result) {
        return Customer.builder()
                .account(account)
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .build();
    }
}
