package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.response.DropCustomerResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropSearchingService {
    private final DropRepository dropRepository;
    private final DropMembershipSearchingService dropMembershipSearchingService;

    public List<DropCustomerResponse> findDrops(AccountAuthentication authentication, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Pageable pageable) {
        final Customer customer = authentication.getCustomer();
        final List<Drop> drops = dropRepository.findDrops(customer, xCoordinate, yCoordinate, radius, member, namePrefix, pageable.getSort());
        return toDropCustomerResponse(drops, customer);
    }

    private List<DropCustomerResponse> toDropCustomerResponse(List<Drop> drops, Customer customer) {
        final List<DropMembership> memberships = dropMembershipSearchingService.findMemberships(drops, customer);
        return drops.stream()
                .map(drop -> toDropCustomerResponse(drop, findDropMembershipForCustomer(customer, memberships)))
                .collect(Collectors.toList());
    }

    private DropCustomerResponse toDropCustomerResponse(Drop drop, DropMembership membership) {
        return DropCustomerResponse.builder()
                .dropName(drop.getName())
                .dropDescription(drop.getDescription())
                .dropUid(drop.getUid())
                .requiresPassword(drop.isRequiresPassword())
                .requiresAccept(drop.isRequiresAccept())
                .xCoordinate(drop.getXCoordinate())
                .yCoordinate(drop.getYCoordinate())
                .estimatedRadiusMeters(drop.getEstimatedRadiusMeters())
                .membershipStatus(membership.getMembershipStatus())
                .companyName(drop.getCompany().getName())
                .companyUid(drop.getCompany().getUid())
                .build();
    }

    private DropMembership findDropMembershipForCustomer(Customer customer, List<DropMembership> memberships) {
        return memberships.stream()
                .filter(membership -> membership.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElse(DropMembership.builder().build());
    }
}
