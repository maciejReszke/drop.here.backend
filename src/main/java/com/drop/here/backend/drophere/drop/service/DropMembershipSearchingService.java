package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropMembershipSearchingService {
    private final DropMembershipRepository dropMembershipRepository;

    // TODO: 01/09/2020  test
    public Page<DropCompanyMembershipResponse> findMemberships(Drop drop, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        return dropMembershipRepository.findMembershipsWithCustomers(
                drop,
                prepareDesiredCustomerSubstring(desiredCustomerSubstring),
                prepareDropMembershipStatuses(membershipStatus),
                pageable)
                .map(this::toResponse);
    }

    private DropCompanyMembershipResponse toResponse(DropMembership dropMembership) {
        final Customer customer = dropMembership.getCustomer();
        return DropCompanyMembershipResponse.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .customerId(customer.getId())
                .membershipStatus(dropMembership.getMembershipStatus())
                .build();
    }

    private DropMembershipStatus[] prepareDropMembershipStatuses(String membershipStatus) {
        return Try.ofSupplier(() -> DropMembershipStatus.valueOf(membershipStatus))
                .map(status -> new DropMembershipStatus[]{status})
                .getOrElseGet(ignore -> DropMembershipStatus.values());
    }

    private String prepareDesiredCustomerSubstring(String desiredCustomerSubstring) {
        return StringUtils.isEmpty(desiredCustomerSubstring)
                ? null
                : desiredCustomerSubstring.toLowerCase();
    }
}
