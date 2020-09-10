package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.repository.CompanyCustomerRelationshipRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyCustomerRelationshipService {
    private final CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;
    private final CompanyMappingService companyMappingService;

    public void handleCustomerBlocking(boolean toBlock, Customer customer, Company company) {
        final boolean isBlocked = isBlocked(company, customer);

        if (toBlock && !isBlocked) {
            blockCustomer(customer, company);
        }

        if (!toBlock && isBlocked) {
            unblockCustomer(customer, company);
        }
    }

    private void unblockCustomer(Customer customer, Company company) {
        getCompanyCustomerRelationship(customer, company)
                .ifPresent(relationship -> {
                    relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
                    relationship.setLastUpdatedAt(LocalDateTime.now());
                    log.info("Unblocking customer {} for company {}", customer.getId(), company.getUid());
                    companyCustomerRelationshipRepository.save(relationship);
                });
    }

    private Optional<CompanyCustomerRelationship> getCompanyCustomerRelationship(Customer customer, Company company) {
        return companyCustomerRelationshipRepository.findByCompanyAndCustomer(company, customer);
    }

    private void blockCustomer(Customer customer, Company company) {
        getCompanyCustomerRelationship(customer, company)
                .ifPresentOrElse(relationship -> blockUsingExistingRelationship(customer, company, relationship),
                        () -> blockUsingNewRelationship(customer, company));
    }

    private void blockUsingNewRelationship(Customer customer, Company company) {
        final CompanyCustomerRelationship relationship = companyMappingService.createActiveRelationship(customer, company);
        relationship.setLastUpdatedAt(LocalDateTime.now());
        block(customer, company, relationship, CompanyCustomerRelationshipStatus.BLOCKED, "Blocking customer {} for company {}");
    }

    private void block(Customer customer, Company company, CompanyCustomerRelationship relationship, CompanyCustomerRelationshipStatus blocked, String s) {
        relationship.setRelationshipStatus(blocked);
        log.info(s, customer.getId(), company.getUid());
        companyCustomerRelationshipRepository.save(relationship);
    }

    private void blockUsingExistingRelationship(Customer customer, Company company, CompanyCustomerRelationship relationship) {
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        log.info("Blocking customer {} for company {}", customer.getId(), company.getUid());
        companyCustomerRelationshipRepository.save(relationship);
    }

    public boolean isBlocked(Company company, Customer customer) {
        return companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED);
    }

    public boolean hasRelationship(Company company, Long customerId) {
        return companyCustomerRelationshipRepository.existsByCompanyAndCustomerId(company, customerId);
    }

    public List<CompanyCustomerRelationship> findRelationships(List<Long> customersIds, Company company) {
        return companyCustomerRelationshipRepository.findByCompanyAndCustomerIdIn(company, customersIds);
    }
}
