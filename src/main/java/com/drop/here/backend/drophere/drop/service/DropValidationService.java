package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.common.exceptions.RestOperationForbiddenException;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropValidationService {
    private final RouteRepository routeRepository;

    public void validateUpdate(Drop drop, AccountProfile profile) {
        if (!isOwnerOrSeller(drop, profile)) {
            throw new RestOperationForbiddenException(String.format(
                    "Profile %s cannot perform drop %s update", profile.getProfileUid(), drop.getUid()),
                    RestExceptionStatusCode.DROP_STATUS_UPDATE_FORBIDDEN);
        }
    }

    private boolean isOwnerOrSeller(Drop drop, AccountProfile profile) {
        return profile.getProfileType() == AccountProfileType.MAIN ||
                routeRepository.existsByProfileAndContainsDrop(profile, drop);
    }

    public void validateDelayedUpdate(Drop drop, DropManagementRequest dropManagementRequest) {
        validateUpdatePresentStatus(drop, EnumSet.of(DropStatus.PREPARED, DropStatus.DELAYED), DropStatus.DELAYED);
        if (dropManagementRequest.getDelayByMinutes() == null) {
            throw new RestIllegalRequestValueException(
                    "In order to delay drop delay by minutes cannot be null",
                    RestExceptionStatusCode.DELAY_DROP_LACK_OF_DELAY_BY_MINUTES);
        }
    }

    private void validateUpdatePresentStatus(Drop drop, EnumSet<DropStatus> desiredStatuses, DropStatus updateToStatus) {
        if (!desiredStatuses.contains(drop.getStatus())) {
            throw new RestIllegalRequestValueException(String.format(
                    "In order to change drop status to %s it must be in %s but was %s", updateToStatus, desiredStatuses
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(",")), drop.getStatus()),
                    RestExceptionStatusCode.DROP_UPDATE_INVALID_STATUS);
        }
    }

    public void validateCancelledUpdate(Drop drop) {
        validateUpdatePresentStatus(drop, EnumSet.of(DropStatus.PREPARED, DropStatus.DELAYED), DropStatus.CANCELLED);
    }

    public void validateFinishedUpdate(Drop drop) {
        validateUpdatePresentStatus(drop, EnumSet.of(DropStatus.LIVE), DropStatus.FINISHED);
    }

    public void validateLiveUpdate(Drop drop) {
        validateUpdatePresentStatus(drop, EnumSet.of(DropStatus.PREPARED, DropStatus.DELAYED), DropStatus.LIVE);
    }
}
