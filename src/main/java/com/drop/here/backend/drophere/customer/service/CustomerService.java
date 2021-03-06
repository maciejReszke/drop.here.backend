package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final ImageService imageService;
    private final CustomerMappingService customerMappingService;
    private final PrivilegeService privilegeService;
    private final CustomerPersistenceService customerPersistenceService;

    @Transactional
    public void createCustomer(Account account, ExternalAuthenticationResult result) {
        final Customer customer = customerMappingService.toCustomer(account, result);
        customerPersistenceService.save(customer);
        account.setCustomer(customer);
        privilegeService.addCustomerCreatedPrivilege(account);
        if (ArrayUtils.isNotEmpty(result.getImage())) {
            final Image image = imageService.createImage(result.getImage(), ImageType.CUSTOMER_IMAGE);
            customer.setImage(image);
        }
        log.info("Creating customer for account via external authentication");
    }

    @Transactional(readOnly = true)
    public CustomerManagementResponse findOwnCustomer(AccountAuthentication authentication) {
        final Customer customer = customerPersistenceService.findOwnCustomer(authentication);
        return customerMappingService.toManagementResponse(customer);
    }

    public ResourceOperationResponse updateCustomer(CustomerManagementRequest customerManagementRequest, AccountAuthentication authentication) {
        return authentication.getCustomer() == null
                ? createCustomer(customerManagementRequest, authentication)
                : updateCustomer(customerManagementRequest, authentication.getCustomer());
    }

    private ResourceOperationResponse createCustomer(CustomerManagementRequest customerManagementRequest, AccountAuthentication authentication) {
        final Customer customer = customerMappingService.createCustomer(customerManagementRequest, authentication.getPrincipal());
        log.info("Creating new customer for account with id {}", authentication.getPrincipal().getId());
        customerPersistenceService.save(customer);
        privilegeService.addCustomerCreatedPrivilege(authentication.getPrincipal());
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, customer.getId());
    }

    private ResourceOperationResponse updateCustomer(CustomerManagementRequest customerManagementRequest, Customer customer) {
        customerMappingService.updateCustomer(customerManagementRequest, customer);
        log.info("Updating customer with id {}", customer.getId());
        customerPersistenceService.save(customer);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, customer.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse updateImage(MultipartFile imagePart, AccountAuthentication authentication) {
        try {
            final Image image = imageService.createImage(imagePart.getBytes(), ImageType.CUSTOMER_IMAGE);
            final Customer customer = authentication.getCustomer();
            customer.setImage(image);
            log.info("Updating image for customer {}", customer.getId());
            customerPersistenceService.save(customer);
            return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, customer.getId());
        } catch (IOException exception) {
            throw new RestIllegalRequestValueException("Invalid image " + exception.getMessage(),
                    RestExceptionStatusCode.UPDATE_CUSTOMER_IMAGE_INVALID_IMAGE);
        }
    }

    @Transactional(readOnly = true)
    public Image findImage(Long customerId) {
        return customerPersistenceService.findByIdWithImage(customerId).getImage();
    }
}
