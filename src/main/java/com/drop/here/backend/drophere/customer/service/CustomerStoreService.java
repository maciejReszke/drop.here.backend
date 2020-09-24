package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerStoreService {
    private final CustomerRepository customerRepository;

    public Mono<Customer> save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> findById(String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Customer with id %s was not found", customerId),
                        RestExceptionStatusCode.CUSTOMER_BY_ID_NOT_FOUND)));
    }

    public Mono<Customer> findOwnCustomer(AccountAuthentication authentication) {
        return customerRepository.findByAccount(authentication.getPrincipal());
    }
}
