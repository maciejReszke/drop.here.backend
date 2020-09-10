package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CompanyDataGenerator {

    public Company company(int i, Account account, Country country) {
        return Company.builder()
                .name("companyName" + i)
                .uid("uid" + i)
                .country(country)
                .visibilityStatus(CompanyVisibilityStatus.VISIBLE)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .account(account)
                .visibilityStatus(CompanyVisibilityStatus.VISIBLE)
                .build();
    }

    public CompanyManagementRequest managementRequest(int i) {
        return CompanyManagementRequest.builder()
                .country("poland")
                .name("name" + i)
                .visibilityStatus(CompanyVisibilityStatus.VISIBLE.name())
                .build();
    }

    public CompanyCustomerRelationship companyCustomerRelationship(Company company, Customer customer) {
        return CompanyCustomerRelationship.builder()
                .company(company)
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .lastUpdatedAt(LocalDateTime.now())
                .relationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE)
                .build();
    }
}
