package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.service.DropValidationService;
import com.drop.here.backend.drophere.spot.dto.SpotMembershipNotificationStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DropDelayedUpdateService implements DropUpdateService {
    private final DropValidationService dropValidationService;
    private final SpotMembershipService spotMembershipService;

    // TODO: 04/10/2020 test, implement (jeszcze tylko wysylanie notyfikacji)
    @Override
    public DropStatus update(Drop drop, Spot spot, Company company, DropManagementRequest dropManagementRequest) {
        dropValidationService.validateDelayedUpdate(drop, dropManagementRequest);
        drop.setStartTime(addDelay(dropManagementRequest, drop.getStartTime()));
        drop.setEndTime(addDelay(dropManagementRequest, drop.getEndTime()));
        drop.setStatus(DropStatus.DELAYED);
        final List<SpotMembership> memberships = spotMembershipService.findToBeNotified(spot, SpotMembershipNotificationStatus.delayed());
        return DropStatus.DELAYED;
    }

    private LocalDateTime addDelay(DropManagementRequest dropManagementRequest, LocalDateTime previousTIme) {
        return previousTIme.plusMinutes(dropManagementRequest.getDelayByMinutes());
    }
}
