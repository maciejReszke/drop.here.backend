package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestOperationForbiddenException;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // TODO: 04/10/2020 test, implement
    public void validateDelayedUpdate(Drop drop, DropManagementRequest dropManagementRequest) {

    }
}
