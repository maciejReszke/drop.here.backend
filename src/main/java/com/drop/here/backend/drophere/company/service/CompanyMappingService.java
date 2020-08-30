package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.CountryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompanyMappingService {
    private final CountryService countryService;

    @Value("${companies.uidGenerator.randomPartLength}")
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

    public void updateCompany(CompanyManagementRequest companyManagementRequest, Company company) {
        final String name = companyManagementRequest.getName().trim();
        company.setLastUpdatedAt(LocalDateTime.now());
        company.setName(name);
        company.setUid(generateUid(name));
        company.setCountry(countryService.findActive(companyManagementRequest.getCountry()));
        company.setVisibilityStatus(CompanyVisibilityStatus.valueOf(companyManagementRequest.getVisibilityStatus()));

    }

    private String generateUid(String name) {
        return name.replace(" ", "-").toLowerCase() + RandomStringUtils.randomAlphanumeric(randomUidPart);
    }

    public Company createCompany(CompanyManagementRequest companyManagementRequest, Account account) {
        final Company company = Company.builder()
                .account(account)
                .createdAt(LocalDateTime.now())
                .build();
        updateCompany(companyManagementRequest, company);
        return company;
    }
}