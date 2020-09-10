package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerStoreService {
    private final CustomerRepository customerRepository;

    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Customer with id %s was not found", customerId),
                        RestExceptionStatusCode.CUSTOMER_BY_ID_NOT_FOUND));
    }

    public Customer findOwnCustomer(AccountAuthentication authentication) {
        return customerRepository.findByAccount(authentication.getPrincipal()).orElse(null);
    }

    public Customer findByIdWithImage(Long customerId) {
        return customerRepository.findByIdWithImage(customerId)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Image for customer %s was not found", customerId),
                        RestExceptionStatusCode.CUSTOMER_IMAGE_WAS_NOT_FOUND));
    }
}
