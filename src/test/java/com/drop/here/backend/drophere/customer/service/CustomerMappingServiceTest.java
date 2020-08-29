package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomerMappingServiceTest {

    @InjectMocks
    private CustomerMappingService customerMappingService;

    @Test
    void givenAccountAndExternalAuthenticationResultWhenToCustomerThenMap() {
        //given
        final Account account = Account.builder().build();
        final ExternalAuthenticationResult externalAuthenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);

        //when
        final Customer result = customerMappingService.toCustomer(account, externalAuthenticationResult);

        //then
        assertThat(result.getFirstName()).isEqualTo(externalAuthenticationResult.getFirstName());
        assertThat(result.getLastName()).isEqualTo(externalAuthenticationResult.getLastName());
        assertThat(result.getAccount()).isEqualTo(account);
    }

}