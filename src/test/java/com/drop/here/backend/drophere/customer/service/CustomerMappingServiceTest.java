package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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
        assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
    }

    @Test
    void givenRequestWhenCreateCustomerThenCreate() {
        //given
        final CustomerManagementRequest request = CustomerDataGenerator.managementRequest(1);
        final Account account = Account.builder().build();

        //when
        final Customer result = customerMappingService.createCustomer(request, account);

        //then
        assertThat(result.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getImage()).isNull();
        assertThat(result.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(result.getLastName()).isEqualTo(request.getLastName());
        assertThat(result.getAccount()).isEqualTo(account);
    }

    @Test
    void givenRequestAndCustomerWhenUpdateCustomerThenUpdate() {
        //given
        final CustomerManagementRequest request = CustomerDataGenerator.managementRequest(1);
        final Customer customer = Customer.builder().build();

        //when
        customerMappingService.updateCustomer(request, customer);

        //then
        assertThat(customer.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(customer.getCreatedAt()).isNull();
        assertThat(customer.getImage()).isNull();
        assertThat(customer.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(request.getLastName());
        assertThat(customer.getAccount()).isNull();
    }

    @Test
    void givenExistingCustomerWhenToManagementResponseThenMap() {
        //given
        final Customer customer = CustomerDataGenerator.customer(1, null);

        //when
        final CustomerManagementResponse response = customerMappingService.toManagementResponse(customer);

        //then
        assertThat(response.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(response.getLastName()).isEqualTo(customer.getLastName());
        assertThat(response.isRegistered()).isTrue();
    }

    @Test
    void givenNotExistingCustomerWhenToManagementResponseThenMap() {
        //given
        final Customer customer = null;

        //when
        final CustomerManagementResponse response = customerMappingService.toManagementResponse(customer);

        //then
        assertThat(response.getFirstName()).isNull();
        assertThat(response.getLastName()).isNull();
        assertThat(response.isRegistered()).isFalse();
    }
}