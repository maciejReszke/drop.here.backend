package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SpotManagementValidationService {
    private final SpotMembershipRepository spotMembershipRepository;

    public void validateSpotRequest(SpotManagementRequest spotManagementRequest) {
        validatePassword(spotManagementRequest);
    }

    private void validatePassword(SpotManagementRequest spotManagementRequest) {
        if (spotManagementRequest.isRequiresPassword() && StringUtils.isBlank(spotManagementRequest.getPassword())) {
            throw new RestIllegalRequestValueException(String.format(
                    "Spot with name %s having password requirement did not provide password", spotManagementRequest.getName()),
                    RestExceptionStatusCode.SPOT_WITH_PASSWORD_REQUIREMENT_LACK_OF_PASSWORD);
        }
    }

    public Mono<Void> validateJoinSpotRequest(Spot spot, SpotJoinRequest spotJoinRequest, Customer customer) {
        validateSpotPassword(spot, spotJoinRequest);
        return validateUniqueness(spot, customer);
    }

    private Mono<Void> validateUniqueness(Spot spot, Customer customer) {
        return spotMembershipRepository.findBySpotAndCustomer(spot, customer)
                .flatMap(spotMembership -> Mono.error(() -> new RestIllegalRequestValueException(String.format(
                        "Customer %s already joined spot %s", customer.getId(), spot.getId()),
                        RestExceptionStatusCode.SPOT_MEMBERSHIP_CUSTOMER_ALREADY_JOINED_SPOT)))
                .then();
    }

    private void validateSpotPassword(Spot spot, SpotJoinRequest spotJoinRequest) {
        if (spot.isRequiresPassword() && !spot.getPassword().equalsIgnoreCase(spotJoinRequest.getPassword())) {
            throw new RestIllegalRequestValueException(String.format(
                    "Invalid password during attempt of joining spot %s", spot.getId()),
                    RestExceptionStatusCode.SPOT_MEMBERSHIP_INVALID_PASSWORD);
        }
    }

    public void validateUpdateMembership(SpotCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        Try.ofSupplier(() -> SpotMembershipStatus.valueOf(companyMembershipManagementRequest.getMembershipStatus()))
                .filter(value -> SpotMembershipStatus.ACTIVE == value || SpotMembershipStatus.BLOCKED == value)
                .getOrElseThrow(ignore -> new RestIllegalRequestValueException(String.format(
                        "During updating membership the only valid status values are %s and %s but was %s",
                        SpotMembershipStatus.ACTIVE, SpotMembershipStatus.BLOCKED, companyMembershipManagementRequest.getMembershipStatus()),
                        RestExceptionStatusCode.UPDATE_MEMBERSHIP_BY_COMPANY_INVALID_MEMBERSHIP_STATUS));
    }

    public void validateDeleteSpotMembership(SpotMembership spotMembership) {
        if (spotMembership.getMembershipStatus() == SpotMembershipStatus.BLOCKED) {
            throw new RestIllegalRequestValueException(String.format(
                    "Cannot delete spot membership with id %s because status is BLOCKED", spotMembership.getId()),
                    RestExceptionStatusCode.SPOT_MEMBERSHIP_DELETE_ATTEMPT_TO_DELETE_BLOCKED_MEMBERSHIP
            );
        }
    }
}
