package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerDataGenerator {

    public Customer customer(int i, Account account) {
        return Customer.builder()
                .firstName("firstName" + i)
                .lastName("lastName" + i)
                .account(account)
                .build();
    }
}
