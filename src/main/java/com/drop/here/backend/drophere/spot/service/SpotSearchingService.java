package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.response.SpotCustomerResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class SpotSearchingService {
    private final SpotRepository spotRepository;
    private final SpotMembershipSearchingService spotMembershipSearchingService;

    public List<SpotCustomerResponse> findSpots(AccountAuthentication authentication, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Pageable pageable) {
        final Customer customer = authentication.getCustomer();
        final List<Spot> spots = spotRepository.findSpots(customer, xCoordinate, yCoordinate, radius, member, namePrefix, pageable.getSort());
        return toSpotCustomerResponse(spots, customer);
    }

    private List<SpotCustomerResponse> toSpotCustomerResponse(List<Spot> spots, Customer customer) {
        final List<SpotMembership> memberships = spotMembershipSearchingService.findMemberships(spots, customer);
        return spots.stream()
                .map(spot -> toSpotCustomerResponse(spot, findSpotMembershipForCustomer(customer, memberships)))
                .collect(Collectors.toList());
    }

    private SpotCustomerResponse toSpotCustomerResponse(Spot spot, SpotMembership membership) {
        return SpotCustomerResponse.builder()
                .spotName(spot.getName())
                .dropDescription(spot.getDescription())
                .spotUid(spot.getUid())
                .requiresPassword(spot.isRequiresPassword())
                .requiresAccept(spot.isRequiresAccept())
                .xCoordinate(spot.getXCoordinate())
                .yCoordinate(spot.getYCoordinate())
                .estimatedRadiusMeters(spot.getEstimatedRadiusMeters())
                .membershipStatus(membership.getMembershipStatus())
                .companyName(spot.getCompany().getName())
                .companyUid(spot.getCompany().getUid())
                .build();
    }

    private SpotMembership findSpotMembershipForCustomer(Customer customer, List<SpotMembership> memberships) {
        return memberships.stream()
                .filter(membership -> membership.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElse(SpotMembership.builder().build());
    }
}
