package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.CountryService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompanyMappingService {
    private final CountryService countryService;

    @Value("${companies.uid_generator.random_part_length}")
    private int randomUidPart;

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

    public Mono<Company> updateCompany(CompanyManagementRequest companyManagementRequest, Company company) {
        return countryService.findActive(companyManagementRequest.getCountry())
                .doOnNext(country -> {
                    final String name = companyManagementRequest.getName().trim();
                    company.setLastUpdatedAt(LocalDateTime.now());
                    company.setName(name);
                    company.setUid(generateUid(name));
                    company.setCountry(country);
                    company.setVisibilityStatus(CompanyVisibilityStatus.valueOf(companyManagementRequest.getVisibilityStatus()));
                })
                .map(ignore -> company);

    }

    private String generateUid(String name) {
        return name.replace(" ", "-").toLowerCase() + RandomStringUtils.randomAlphanumeric(randomUidPart);
    }

    public Mono<Company> createCompany(CompanyManagementRequest companyManagementRequest, Account account) {
        final Company company = Company.builder()
                .account(account)
                .createdAt(LocalDateTime.now())
                .build();
        return updateCompany(companyManagementRequest, company);
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
