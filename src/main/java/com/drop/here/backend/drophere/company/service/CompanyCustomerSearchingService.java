package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerDropMembershipResponse;
import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerSearchingService;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.service.DropMembershipService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyCustomerSearchingService {
    private final CustomerSearchingService customerSearchingService;
    private final DropMembershipService dropMembershipService;
    private final CompanyCustomerRelationshipService companyCustomerRelationshipService;

    public Page<CompanyCustomerResponse> findCustomers(String desiredCustomerStartingSubstring, Boolean blocked, AccountAuthentication authentication, Pageable pageable) {
        final Company company = authentication.getCompany();
        final Page<Customer> customers = customerSearchingService.findCustomers(desiredCustomerStartingSubstring, blocked, company, pageable);
        return mapToCompanyCustomerResponses(customers, company);
    }

    private Page<CompanyCustomerResponse> mapToCompanyCustomerResponses(Page<Customer> customers, Company company) {
        final List<Long> customersIds = customers.stream()
                .map(Customer::getId)
                .distinct()
                .collect(Collectors.toList());

        final List<DropMembership> dropMemberships = dropMembershipService.findMembershipsJoinFetchDrops(customersIds, company);
        final List<CompanyCustomerRelationship> relationships = companyCustomerRelationshipService.findRelationships(customersIds, company);

        return customers.map(customer -> toCompanyCustomerResponse(
                customer,
                findDropMembershipsForCustomer(customer, dropMemberships),
                findRelationshipForCustomer(customer, relationships)
        ));
    }

    private CompanyCustomerRelationship findRelationshipForCustomer(Customer customer, List<CompanyCustomerRelationship> relationships) {
        return relationships.stream()
                .filter(companyCustomerRelationship -> companyCustomerRelationship.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElseGet(() -> CompanyCustomerRelationship.builder().relationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE).build());
    }

    private List<DropMembership> findDropMembershipsForCustomer(Customer customer, List<DropMembership> dropMemberships) {
        return dropMemberships.stream()
                .filter(dropMembership -> dropMembership.getCustomer().getId().equals(customer.getId()))
                .sorted(Comparator.comparing(DropMembership::getId))
                .collect(Collectors.toList());
    }

    private CompanyCustomerResponse toCompanyCustomerResponse(Customer customer, List<DropMembership> dropMemberships, CompanyCustomerRelationship relationshipForCustomer) {
        return CompanyCustomerResponse.builder()
                .customerId(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .relationshipStatus(relationshipForCustomer.getRelationshipStatus())
                .companyCustomerDropMemberships(toCompanyCustomerDropMembershipResponse(dropMemberships))
                .build();
    }

    private List<CompanyCustomerDropMembershipResponse> toCompanyCustomerDropMembershipResponse(List<DropMembership> dropMemberships) {
        return dropMemberships.stream()
                .map(dropMembership -> CompanyCustomerDropMembershipResponse.builder()
                        .membershipStatus(dropMembership.getMembershipStatus())
                        .dropId(dropMembership.getId())
                        .dropName(dropMembership.getDrop().getName())
                        .dropUid(dropMembership.getDrop().getUid())
                        .build())
                .collect(Collectors.toList());
    }
}
