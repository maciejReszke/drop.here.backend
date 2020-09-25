package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerStoreService {
    private final CustomerRepository customerRepository;

    public Mono<Customer> update(Customer customer) {
        log.info("Updating customer with id {}", customer.getId());
        return customerRepository.save(customer);
    }

    public Mono<Customer> findById(String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Customer with id %s was not found", customerId),
                        RestExceptionStatusCode.CUSTOMER_BY_ID_NOT_FOUND)));
    }
}
