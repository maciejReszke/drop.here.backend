package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import io.vavr.control.Try;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DropManagementValidationService {

    public void validateDropRequest(DropManagementRequest dropManagementRequest) {
        validateLocationType(dropManagementRequest);
        validatePassword(dropManagementRequest);
    }

    private void validatePassword(DropManagementRequest dropManagementRequest) {
        if (dropManagementRequest.isRequiresPassword() && StringUtils.isBlank(dropManagementRequest.getPassword())) {
            throw new RestIllegalRequestValueException(String.format(
                    "Drop with name %s having password requirement did not provide password", dropManagementRequest.getName()),
                    RestExceptionStatusCode.DROP_WITH_PASSWORD_REQUIREMENT_LACK_OF_PASSWORD);
        }
    }

    private void validateLocationType(DropManagementRequest dropManagementRequest) {
        final DropLocationType locationType = Try.ofSupplier(() -> DropLocationType.valueOf(dropManagementRequest.getLocationDropType()))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "Drop with name %s has invalid location type %s", dropManagementRequest.getName(), dropManagementRequest.getLocationDropType()),
                        RestExceptionStatusCode.DROP_INVALID_LOCATION_TYPE));

        if (locationType == DropLocationType.GEOLOCATION && anyLocationPropertyNull(dropManagementRequest)) {
            throw new RestIllegalRequestValueException(String.format(
                    "Drop with name %s having location type GEOLOCATION has some location property null", dropManagementRequest.getName()),
                    RestExceptionStatusCode.DROP_GEOLOCATION_NULL_LOCATION_PROPERTY);
        }
    }

    private boolean anyLocationPropertyNull(DropManagementRequest dropManagementRequest) {
        return ObjectUtils.anyNull(
                dropManagementRequest.getXCoordinate(),
                dropManagementRequest.getYCoordinate(),
                dropManagementRequest.getEstimatedRadiusMeters());
    }

    // TODO: 28/08/2020 test, implement (unikalne)
    public void validateCreatingDropMembershipRequest(Drop drop, DropJoinRequest dropJoinRequest) {

    }
}
