package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.service.UidGeneratorService;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.CountryService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompanyMappingService {
    private final CountryService countryService;
    private final UidGeneratorService uidGeneratorService;

    @Value("${companies.uid_generator.random_part_length}")
    private int randomUidPart;

    @Value("${companies.uid_generator.name_part_length}")
    private int namePartLength;

    @Transactional(readOnly = true)
    public CompanyManagementResponse toManagementResponse(Company company) {
        return company == null ?
                CompanyManagementResponse.builder().registered(false).build()
                : CompanyManagementResponse.builder()
                .registered(true)
                .country(company.getCountry().getName())
                .name(company.getName())
                .uid(company.getUid())
                .visibilityStatus(company.getVisibilityStatus())
                .build();
    }

    public void updateCompany(CompanyManagementRequest companyManagementRequest, Company company) {
        final String name = companyManagementRequest.getName().trim();
        company.setLastUpdatedAt(LocalDateTime.now());
        company.setName(name);
        company.setUid(generateUid(name));
        company.setCountry(countryService.findActive(companyManagementRequest.getCountry()));
        company.setVisibilityStatus(CompanyVisibilityStatus.valueOf(companyManagementRequest.getVisibilityStatus()));
    }

    private String generateUid(String name) {
        return uidGeneratorService.generateUid(name, namePartLength, randomUidPart);
    }

    public Company createCompany(CompanyManagementRequest companyManagementRequest, Account account) {
        final Company company = Company.builder()
                .account(account)
                .createdAt(LocalDateTime.now())
                .build();
        updateCompany(companyManagementRequest, company);
        return company;
    }

    public CompanyCustomerRelationship createActiveRelationship(Customer customer, Company company) {
        return CompanyCustomerRelationship.builder()
                .relationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE)
                .lastUpdatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .company(company)
                .build();
    }
}
