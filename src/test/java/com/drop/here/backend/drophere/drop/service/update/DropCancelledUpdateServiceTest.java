package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.service.DropValidationService;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.spot.dto.SpotMembershipNotificationStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropCancelledUpdateServiceTest {

    @InjectMocks
    private DropCancelledUpdateService dropCancelledUpdateService;

    @Mock
    private DropValidationService dropValidationService;

    @Mock
    private SpotMembershipService spotMembershipService;

    @Mock
    private NotificationService notificationService;

    @Test
    void givenDropSpotAndRequestForceWhenUpdateThenUpdateAndSendNotifications() {
        //given
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusMinutes(15)).build();
        final Spot spot = Spot.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final Company company = Company.builder().build();
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();

        doNothing().when(notificationService).createNotifications(any());
        when(spotMembershipService.findToBeNotified(spot, SpotMembershipNotificationStatus.cancelled())).thenReturn(List.of());

        //when
        final DropStatus result = dropCancelledUpdateService.update(drop, spot, company, accountProfile, dropManagementRequest, true);

        //then
        verifyNoInteractions(dropValidationService);
        assertThat(result).isEqualTo(DropStatus.CANCELLED);
        assertThat(drop.getCancelledAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }

    @Test
    void givenDropSpotAndRequestNotForceWhenUpdateThenUpdateAndSendNotifications() {
        //given
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusMinutes(15)).build();
        final Spot spot = Spot.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final Company company = Company.builder().build();
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();

        doNothing().when(dropValidationService).validateCancelledUpdate(drop);
        doNothing().when(notificationService).createNotifications(any());
        when(spotMembershipService.findToBeNotified(spot, SpotMembershipNotificationStatus.cancelled())).thenReturn(List.of());

        //when
        final DropStatus result = dropCancelledUpdateService.update(drop, spot, company, accountProfile, dropManagementRequest, false);

        //then
        assertThat(result).isEqualTo(DropStatus.CANCELLED);
        assertThat(drop.getCancelledAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }
}