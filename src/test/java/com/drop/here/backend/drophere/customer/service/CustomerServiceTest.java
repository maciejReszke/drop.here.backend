package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerStoreService customerStoreService;

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
        doNothing().when(customerStoreService).save(customer);
        when(imageService.updateImage(externalAuthenticationResult.getImage(), ImageType.CUSTOMER_IMAGE))
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
        doNothing().when(customerStoreService).save(customer);
        doNothing().when(privilegeService).addCustomerCreatedPrivilege(account);

        //when
        customerService.createCustomer(account, externalAuthenticationResult);

        //then
        assertThat(account.getCustomer()).isEqualTo(customer);
        assertThat(customer.getImage()).isNull();
    }

    @Test
    void givenImageWhenUpdateImageThenUpdate() throws IOException {
        //given
        final MockMultipartFile image = new MockMultipartFile("name", "byte".getBytes());
        final Account account = Account.builder().build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .customer(customer)
                .build();

        final Image imageEntity = Image.builder().build();
        when(imageService.updateImage(image.getBytes(), ImageType.CUSTOMER_IMAGE))
                .thenReturn(imageEntity);
        doNothing().when(customerStoreService).save(customer);

        //when
        final ResourceOperationResponse resourceOperationResponse = customerService.updateImage(image, accountAuthentication);

        //then
        assertThat(resourceOperationResponse.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(customer.getImage()).isEqualTo(imageEntity);
    }


    @Test
    void givenExistingCustomerWhenUpdateCustomerThenUpdate() {
        //given
        final CustomerManagementRequest request = CustomerDataGenerator.managementRequest(1);
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();

        doNothing().when(customerMappingService).updateCustomer(request, customer);
        doNothing().when(customerStoreService).save(customer);
        //when
        final ResourceOperationResponse result = customerService.updateCustomer(request, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        verifyNoInteractions(privilegeService);
    }

    @Test
    void givenNotExistingCustomerWhenUpdateCustomerThenCreate() {
        //given
        final CustomerManagementRequest request = CustomerDataGenerator.managementRequest(1);
        final Account account = Account.builder().build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .build();

        when(customerMappingService.createCustomer(request, account)).thenReturn(customer);
        doNothing().when(customerStoreService).save(customer);
        doNothing().when(privilegeService).addCustomerCreatedPrivilege(account);

        //when
        final ResourceOperationResponse result = customerService.updateCustomer(request, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenAccountAuthenticationWhenFindOwnCustomerThenFind() {
        //given
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final CustomerManagementResponse customerManagementResponse = CustomerManagementResponse.builder().build();

        when(customerMappingService.toManagementResponse(accountAuthentication.getCustomer()))
                .thenReturn(customerManagementResponse);

        //when
        final CustomerManagementResponse response = customerService.findOwnCustomer(accountAuthentication);

        //then
        assertThat(response).isEqualTo(customerManagementResponse);
    }

    @Test
    void givenExistingCustomerWithImageWhenFindImageThenFind() {
        //given
        final Long customerId = 1L;
        final Image image = Image.builder().build();
        final Customer customer = Customer.builder()
                .image(image)
                .build();

        when(customerStoreService.findByIdWithImage(customerId)).thenReturn(customer);
        //when
        final Image result = customerService.findImage(customerId);

        //then
        assertThat(result).isEqualTo(image);
    }
}