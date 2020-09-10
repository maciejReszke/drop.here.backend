package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerStoreServiceTest {

    @InjectMocks
    private CustomerStoreService customerStoreService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void givenExistingCustomerWhenFindByIdThenFind() {
        //given
        final Long customerId = 1L;
        final Customer customer = CustomerDataGenerator.customer(1, null);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        //when
        final Customer result = customerStoreService.findById(customerId);

        //then
        assertThat(result).isEqualTo(customer);
    }

    @Test
    void givenNotExistingCustomerWhenFindByIdThenThrow() {
        //given
        final Long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> customerStoreService.findById(customerId));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }


}