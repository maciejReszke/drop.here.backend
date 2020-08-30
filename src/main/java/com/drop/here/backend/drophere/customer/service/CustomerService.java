package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ImageService imageService;
    private final CustomerMappingService customerMappingService;
    private final PrivilegeService privilegeService;

    @Transactional
    public void createCustomer(Account account, ExternalAuthenticationResult result) {
        final Customer customer = customerMappingService.toCustomer(account, result);
        customerRepository.save(customer);
        account.setCustomer(customer);
        privilegeService.addCustomerCreatedPrivilege(account);
        if (ArrayUtils.isNotEmpty(result.getImage())) {
            final Image image = imageService.createImage(result.getImage(), ImageType.CUSTOMER_IMAGE);
            customer.setImage(image);
        }
        log.info("Creating customer for account via external authentication");
    }
}
