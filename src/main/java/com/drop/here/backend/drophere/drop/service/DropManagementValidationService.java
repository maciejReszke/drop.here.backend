package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropManagementValidationService {
    private final DropMembershipRepository dropMembershipRepository;

    public void validateDropRequest(DropManagementRequest dropManagementRequest) {
        validatePassword(dropManagementRequest);
    }

    private void validatePassword(DropManagementRequest dropManagementRequest) {
        if (dropManagementRequest.isRequiresPassword() && StringUtils.isBlank(dropManagementRequest.getPassword())) {
            throw new RestIllegalRequestValueException(String.format(
                    "Drop with name %s having password requirement did not provide password", dropManagementRequest.getName()),
                    RestExceptionStatusCode.DROP_WITH_PASSWORD_REQUIREMENT_LACK_OF_PASSWORD);
        }
    }

    public void validateJoinDropRequest(Drop drop, DropJoinRequest dropJoinRequest, Customer customer) {
        validateDropPassword(drop, dropJoinRequest);
        validateUniqueness(drop, customer);
    }

    private void validateUniqueness(Drop drop, Customer customer) {
        if (dropMembershipRepository.findByDropAndCustomer(drop, customer).isPresent()) {
            throw new RestIllegalRequestValueException(String.format(
                    "Customer %s already joined drop %s", customer.getId(), drop.getId()),
                    RestExceptionStatusCode.DROP_MEMBERSHIP_CUSTOMER_ALREADY_JOINED_DROP
            );
        }
    }

    private void validateDropPassword(Drop drop, DropJoinRequest dropJoinRequest) {
        if (drop.isRequiresPassword() && !drop.getPassword().equalsIgnoreCase(dropJoinRequest.getPassword())) {
            throw new RestIllegalRequestValueException(String.format(
                    "Invalid password during attempt of joining drop %s", drop.getId()),
                    RestExceptionStatusCode.DROP_MEMBERSHIP_INVALID_PASSWORD);
        }
    }

    public void validateUpdateMembership(DropCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        Try.ofSupplier(() -> DropMembershipStatus.valueOf(companyMembershipManagementRequest.getMembershipStatus()))
                .filter(value -> DropMembershipStatus.ACTIVE == value || DropMembershipStatus.BLOCKED == value)
                .getOrElseThrow(ignore -> new RestIllegalRequestValueException(String.format(
                        "During updating membership the only valid status values are %s and %s but was %s",
                        DropMembershipStatus.ACTIVE, DropMembershipStatus.BLOCKED, companyMembershipManagementRequest.getMembershipStatus()),
                        RestExceptionStatusCode.UPDATE_MEMBERSHIP_BY_COMPANY_INVALID_MEMBERSHIP_STATUS));
    }

    public void validateDeleteDropMembership(DropMembership dropMembership) {
        if (dropMembership.getMembershipStatus() == DropMembershipStatus.BLOCKED) {
            throw new RestIllegalRequestValueException(String.format(
                    "Cannot delete drop membership with id %s because status is BLOCKED", dropMembership.getId()),
                    RestExceptionStatusCode.DROP_MEMBERSHIP_DELETE_ATTEMPT_TO_DELETE_BLOCKED_MEMBERSHIP
            );
        }
    }
}
