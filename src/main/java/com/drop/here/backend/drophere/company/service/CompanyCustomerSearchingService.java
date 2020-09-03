package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.controller.CompanyCustomerResponse;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerSearchingService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyCustomerSearchingService {
    private final CustomerSearchingService customerSearchingService;

    // TODO: 02/09/2020 test
    public Page<CompanyCustomerResponse> findCustomers(String desiredCustomerStartingSubstring, Boolean blocked, AccountAuthentication authentication, Pageable pageable) {
        return customerSearchingService.findCustomers(desiredCustomerStartingSubstring, blocked, authentication.getCompany(), pageable)
                .map(customer -> toCompanyCustomerResponse(customer));
    }

    // TODO: 02/09/2020  implement wraz z informacja o regionach i ewentualnych banach
    private CompanyCustomerResponse toCompanyCustomerResponse(Customer customer) {
        return null;
    }
}
