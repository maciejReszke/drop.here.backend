package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotMembershipSearchingService {
    private final SpotMembershipRepository spotMembershipRepository;

    public Page<SpotCompanyMembershipResponse> findMemberships(Spot spot, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        return spotMembershipRepository.findMembershipsWithCustomers(
                spot,
                prepareDesiredCustomerSubstring(desiredCustomerSubstring),
                prepareSpotMembershipStatuses(membershipStatus),
                pageable)
                .map(this::toResponse);
    }

    private SpotCompanyMembershipResponse toResponse(SpotMembership spotMembership) {
        final Customer customer = spotMembership.getCustomer();
        return SpotCompanyMembershipResponse.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .customerId(customer.getId())
                .membershipStatus(spotMembership.getMembershipStatus())
                .build();
    }

    private SpotMembershipStatus[] prepareSpotMembershipStatuses(String membershipStatus) {
        return Try.ofSupplier(() -> SpotMembershipStatus.valueOf(membershipStatus))
                .map(status -> new SpotMembershipStatus[]{status})
                .getOrElseGet(ignore -> SpotMembershipStatus.values());
    }

    private String prepareDesiredCustomerSubstring(String desiredCustomerSubstring) {
        return StringUtils.isEmpty(desiredCustomerSubstring)
                ? null
                : desiredCustomerSubstring.toLowerCase() + '%';
    }

    public List<SpotMembership> findMemberships(List<Spot> spots, Customer customer) {
        return spotMembershipRepository.findByCustomerAndSpotIn(customer, spots);
    }
}
