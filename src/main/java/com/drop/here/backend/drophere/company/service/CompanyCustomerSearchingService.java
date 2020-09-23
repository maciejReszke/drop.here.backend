package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerSpotMembershipResponse;
import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerSearchingService;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class CompanyCustomerSearchingService {
    private final CustomerSearchingService customerSearchingService;
    private final SpotMembershipService spotMembershipService;
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

        final List<SpotMembership> spotMemberships = spotMembershipService.findMembershipsJoinFetchSpots(customersIds, company);
        final List<CompanyCustomerRelationship> relationships = companyCustomerRelationshipService.findRelationships(customersIds, company);

        return customers.map(customer -> toCompanyCustomerResponse(
                customer,
                findSpotMembershipsForCustomer(customer, spotMemberships),
                findRelationshipForCustomer(customer, relationships)
        ));
    }

    private CompanyCustomerRelationship findRelationshipForCustomer(Customer customer, List<CompanyCustomerRelationship> relationships) {
        return relationships.stream()
                .filter(companyCustomerRelationship -> companyCustomerRelationship.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElseGet(() -> CompanyCustomerRelationship.builder().relationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE).build());
    }

    private List<SpotMembership> findSpotMembershipsForCustomer(Customer customer, List<SpotMembership> spotMemberships) {
        return spotMemberships.stream()
                .filter(spotMembership -> spotMembership.getCustomer().getId().equals(customer.getId()))
                .sorted(Comparator.comparing(SpotMembership::getId))
                .collect(Collectors.toList());
    }

    private CompanyCustomerResponse toCompanyCustomerResponse(Customer customer, List<SpotMembership> spotMemberships, CompanyCustomerRelationship relationshipForCustomer) {
        return CompanyCustomerResponse.builder()
                .customerId(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .relationshipStatus(relationshipForCustomer.getRelationshipStatus())
                .companyCustomerSpotMemberships(toCompanyCustomerSpotMembershipResponse(spotMemberships))
                .build();
    }

    private List<CompanyCustomerSpotMembershipResponse> toCompanyCustomerSpotMembershipResponse(List<SpotMembership> spotMemberships) {
        return spotMemberships.stream()
                .map(spotMembership -> CompanyCustomerSpotMembershipResponse.builder()
                        .membershipStatus(spotMembership.getMembershipStatus())
                        .spotId(spotMembership.getId())
                        .spotName(spotMembership.getSpot().getName())
                        .spotUid(spotMembership.getSpot().getUid())
                        .build())
                .collect(Collectors.toList());
    }
}
