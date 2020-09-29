package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.response.SpotBaseCustomerResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotDetailedCustomerResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotSearchingService {
    private final SpotRepository spotRepository;
    private final SpotMembershipSearchingService spotMembershipSearchingService;
    private final DropService dropService;

    @Value("${spots.spot_response.spot_drops_for_days}")
    private Integer spotResponseDropsForDays;

    public List<SpotBaseCustomerResponse> findSpots(AccountAuthentication authentication, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Pageable pageable) {
        final Customer customer = authentication.getCustomer();
        final List<Spot> spots = spotRepository.findSpots(customer, xCoordinate, yCoordinate, radius, member, namePrefix, pageable.getSort());
        return toSpotCustomerResponse(spots, customer);
    }

    private List<SpotBaseCustomerResponse> toSpotCustomerResponse(List<Spot> spots, Customer customer) {
        final List<SpotMembership> memberships = spotMembershipSearchingService.findMemberships(spots, customer);
        return spots.stream()
                .map(spot -> toSpotCustomerResponse(spot, findSpotMembershipForCustomer(customer, memberships)))
                .collect(Collectors.toList());
    }

    private SpotBaseCustomerResponse toSpotCustomerResponse(Spot spot, SpotMembership membership) {
        return SpotBaseCustomerResponse.builder()
                .name(spot.getName())
                .description(spot.getDescription())
                .uid(spot.getUid())
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

    private Spot findAvailableSpot(String spotUid, String companyUid, Customer customer) {
        return spotRepository.findAvailableSpot(spotUid, companyUid, customer)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Spot with uid %s company %s was not found or is not available", spotUid, companyUid),
                        RestExceptionStatusCode.AVAILABLE_FOR_CUSTOMER_SPOT_NOT_FOUND));
    }

    public SpotDetailedCustomerResponse findSpot(String spotUid, String companyUid, AccountAuthentication authentication) {
        final Customer customer = authentication.getCustomer();
        final Spot spot = findAvailableSpot(spotUid, companyUid, customer);
        final SpotMembership spotMembership = spotMembershipSearchingService.findMembership(spot, customer)
                .orElse(SpotMembership.builder().build());
        final SpotBaseCustomerResponse baseCustomerResponse = toSpotCustomerResponse(spot, spotMembership);
        final LocalDateTime nowAtStartOfDay = LocalDate.now().atStartOfDay();
        return new SpotDetailedCustomerResponse(dropService.findDrops(spot, nowAtStartOfDay, nowAtStartOfDay.plusDays(spotResponseDropsForDays)), baseCustomerResponse);
    }
}
