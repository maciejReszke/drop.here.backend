package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.service.DropValidationService;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.spot.dto.SpotMembershipNotificationStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropCancelledUpdateService implements DropUpdateService {
    private final DropValidationService dropValidationService;
    private final SpotMembershipService spotMembershipService;
    private final NotificationService notificationService;

    @Override
    public DropStatus update(Drop drop, Spot spot, Company company, AccountProfile profile, DropManagementRequest dropManagementRequest) {
        dropValidationService.validateCancelledUpdate(drop);
        final List<SpotMembership> memberships = spotMembershipService.findToBeNotified(spot, SpotMembershipNotificationStatus.cancelled());
        notificationService.createNotifications(prepareNotificationRequest(memberships, drop, company, profile));
        drop.setCancelledAt(LocalDateTime.now());
        return DropStatus.CANCELLED;
    }

    private NotificationCreationRequest prepareNotificationRequest(List<SpotMembership> memberships, Drop drop, Company company, AccountProfile profile) {
        return NotificationCreationRequest.builder()
                .title(String.format("%s alert!", company.getName()))
                .message(String.format("%s is now %s!", company.getName(), DropStatus.CANCELLED.name()))
                .notificationType(NotificationType.PUSH_NOTIFICATION_ONLY)
                .notificationCategory(NotificationCategory.DROP_STATUS_CHANGE)
                .broadcastingType(NotificationBroadcastingType.COMPANY)
                .broadcastingCompany(company)
                .referencedSubjectType(NotificationReferencedSubjectType.DROP)
                .referencedSubjectId(drop.getUid())
                .recipientCustomers(memberships.stream().map(SpotMembership::getCustomer).collect(Collectors.toList()))
                .recipientAccountProfiles(profile != null ? List.of(profile) : List.of())
                .build();
    }
}
