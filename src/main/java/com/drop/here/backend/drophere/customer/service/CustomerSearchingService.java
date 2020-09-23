package com.drop.here.backend.drophere.customer.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class CustomerSearchingService {
    private final CustomerRepository customerRepository;

    public Page<Customer> findCustomers(String desiredCustomerStartingSubstring, Boolean blocked, Company company, Pageable pageable) {
        return customerRepository.findCustomers(desiredCustomerStartingSubstring, blocked, company, pageable);
    }
}
