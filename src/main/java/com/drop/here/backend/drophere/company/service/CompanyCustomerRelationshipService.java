package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.repository.CompanyCustomerRelationshipRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyCustomerRelationshipService {
    private final CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;
    private final CompanyMappingService companyMappingService;

    public Mono<Void> handleCustomerBlocking(boolean toBlock, Customer customer, Company company) {
        return isBlocked(company, customer)
                .flatMap(isBlocked -> handleCustomerBlocking(isBlocked, toBlock, customer, company));
    }

    private Mono<Void> handleCustomerBlocking(Boolean isBlocked, boolean toBlock, Customer customer, Company company) {
        if (toBlock && !isBlocked) {
            return blockCustomer(customer, company).then();
        }

        if (!toBlock && isBlocked) {
            return unblockCustomer(customer, company).then();
        }

        return Mono.empty();
    }

    private Mono<CompanyCustomerRelationship> unblockCustomer(Customer customer, Company company) {
        return getCompanyCustomerRelationship(customer, company)
                .doOnNext(relationship -> {
                    relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
                    relationship.setLastUpdatedAt(LocalDateTime.now());
                    log.info("Unblocking customer {} for company {}", customer.getId(), company.getUid());
                })
                .flatMap(companyCustomerRelationshipRepository::save);
    }

    private Mono<CompanyCustomerRelationship> getCompanyCustomerRelationship(Customer customer, Company company) {
        return companyCustomerRelationshipRepository.findByCompanyAndCustomerId(company, customer.getId());
    }

    private Mono<CompanyCustomerRelationship> blockCustomer(Customer customer, Company company) {
        return getCompanyCustomerRelationship(customer, company)
                .flatMap(companyCustomerRelationship -> blockUsingExistingRelationship(customer, company, companyCustomerRelationship))
                .switchIfEmpty(Mono.defer(() -> blockUsingNewRelationship(customer, company)));
    }

    private Mono<CompanyCustomerRelationship> blockUsingNewRelationship(Customer customer, Company company) {
        final CompanyCustomerRelationship relationship = companyMappingService.createActiveRelationship(customer, company);
        relationship.setLastUpdatedAt(LocalDateTime.now());
        return block(customer, company, relationship);
    }

    private Mono<CompanyCustomerRelationship> block(Customer customer, Company company, CompanyCustomerRelationship relationship) {
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        log.info("Blocking customer {} for company {}", customer.getId(), company.getUid());
        return companyCustomerRelationshipRepository.save(relationship);
    }

    private Mono<CompanyCustomerRelationship> blockUsingExistingRelationship(Customer customer, Company company, CompanyCustomerRelationship relationship) {
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        log.info("Blocking customer {} for company {}", customer.getId(), company.getUid());
        return companyCustomerRelationshipRepository.save(relationship);
    }

    public Mono<Boolean> isBlocked(Company company, Customer customer) {
        return companyCustomerRelationshipRepository.findByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED)
                .map(ignore -> true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<Boolean> hasRelationship(Company company, String customerId) {
        return companyCustomerRelationshipRepository.findByCompanyAndCustomerId(company, customerId)
                .map(ignore -> true)
                .switchIfEmpty(Mono.just(false));

    }

    public List<CompanyCustomerRelationship> findRelationships(List<String> customersIds, Company company) {
        return companyCustomerRelationshipRepository.findByCompanyAndCustomerIdIn(company, customersIds);
    }
}
