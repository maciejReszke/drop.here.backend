package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountPersistenceService;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final ImageService imageService;
    private final CustomerMappingService customerMappingService;
    private final PrivilegeService privilegeService;
    private final CustomerStoreService customerStoreService;
    private final AccountPersistenceService accountPersistenceService;


    // TODO: 24/09/2020 transakcja
    public Mono<Customer> createCustomer(Account account, ExternalAuthenticationResult result) {
        final Customer customer = customerMappingService.toCustomer(account, result);
        privilegeService.addCustomerCreatedPrivilege(account);
        return accountPersistenceService.updateAccount(account)
                .flatMap(saved -> customerStoreService.save(customer))
                .flatMap(saved -> createImageIfNotEmpty(result.getImage(), saved))
                .doOnNext(saved -> log.info("Creating customer for account via external authentication with id {}", account.getId()))
                .thenReturn(customer);
    }

    private Mono<Customer> createImageIfNotEmpty(byte[] image, Customer customer) {
        return ArrayUtils.isNotEmpty(image)
                ? imageService.updateImage(image, ImageType.CUSTOMER_IMAGE, customer.getId())
                .map(ignore -> customer)
                : Mono.just(customer);
    }

    public Mono<CustomerManagementResponse> findOwnCustomer(AccountAuthentication authentication) {
        return customerStoreService.findOwnCustomer(authentication)
                .map(customerMappingService::toManagementResponse)
                .switchIfEmpty(Mono.defer(() -> Mono.just(customerMappingService.toManagementResponse(null))));
    }

    public Mono<ResourceOperationResponse> updateCustomer(CustomerManagementRequest customerManagementRequest, AccountAuthentication authentication) {
        return authentication.getCustomer() == null
                ? createCustomer(customerManagementRequest, authentication)
                : updateCustomer(customerManagementRequest, authentication.getCustomer());
    }

    // TODO: 24/09/2020 transakcja
    private Mono<ResourceOperationResponse> createCustomer(CustomerManagementRequest customerManagementRequest, AccountAuthentication authentication) {
        final Account account = authentication.getPrincipal();
        final Customer customer = customerMappingService.createCustomer(customerManagementRequest, account);
        log.info("Creating new customer for account with id {}", account.getId());
        privilegeService.addCustomerCreatedPrivilege(account);
        return accountPersistenceService.updateAccount(account)
                .flatMap(saved -> customerStoreService.save(customer))
                .map(createdCustomer -> new ResourceOperationResponse(ResourceOperationStatus.CREATED, createdCustomer.getId()));
    }

    private Mono<ResourceOperationResponse> updateCustomer(CustomerManagementRequest customerManagementRequest, Customer customer) {
        customerMappingService.updateCustomer(customerManagementRequest, customer);
        log.info("Updating customer with id {}", customer.getId());
        return customerStoreService.save(customer)
                .map(saved -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, customer.getId()));
    }

    public Mono<ResourceOperationResponse> updateImage(FilePart imagePart, AccountAuthentication authentication) {
        final Customer customer = authentication.getCustomer();
        return imageService.updateImage(imagePart, ImageType.CUSTOMER_IMAGE, customer.getId())
                .map(image -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, customer.getId()));
    }

    public Mono<Image> findImage(String customerId) {
        return imageService.findImage(customerId, ImageType.CUSTOMER_IMAGE);
    }
}
