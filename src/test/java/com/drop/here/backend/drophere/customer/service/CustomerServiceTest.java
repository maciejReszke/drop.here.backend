package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private CustomerMappingService customerMappingService;

    @Mock
    private PrivilegeService privilegeService;

    @Test
    void givenAccountAndExternalAuthenticationResultWithImageWhenCreateCustomerThenCreate() {
        //given
        final Account account = Account.builder().build();
        final ExternalAuthenticationResult externalAuthenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);
        final Image image = Image.builder().build();
        final Customer customer = CustomerDataGenerator.customer(1, account);

        when(customerMappingService.toCustomer(account, externalAuthenticationResult)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(imageService.createImage(externalAuthenticationResult.getImage(), ImageType.CUSTOMER_IMAGE))
                .thenReturn(image);
        doNothing().when(privilegeService).addCustomerCreatedPrivilege(account);

        //when
        customerService.createCustomer(account, externalAuthenticationResult);

        //then
        assertThat(account.getCustomer()).isEqualTo(customer);
        assertThat(customer.getImage()).isEqualTo(image);
    }

    @Test
    void givenAccountAndExternalAuthenticationResultWithoutImageWhenCreateCustomerThenCreate() {
        //given
        final Account account = Account.builder().build();
        final ExternalAuthenticationResult externalAuthenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1)
                .toBuilder()
                .image(null)
                .build();
        final Customer customer = CustomerDataGenerator.customer(1, account);

        when(customerMappingService.toCustomer(account, externalAuthenticationResult)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        doNothing().when(privilegeService).addCustomerCreatedPrivilege(account);

        //when
        customerService.createCustomer(account, externalAuthenticationResult);

        //then
        assertThat(account.getCustomer()).isEqualTo(customer);
        assertThat(customer.getImage()).isNull();
    }


}