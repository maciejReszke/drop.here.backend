package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.stereotype.Service;

@Service
public class CompanyCustomerBlockingService {

    // TODO: 02/09/2020 test, imlement = trzeba wyrzucic wszystkei relacje jezeli byly
    public void handleCustomerBlocking(boolean block, Customer customer, Company company) {

    }

    // TODO: 02/09/2020
    public boolean isBlocked(Company company, Customer customer) {
        return false;
    }
}
