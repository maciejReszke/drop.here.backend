package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropPreparedUpdateServiceTest {
    @InjectMocks
    private DropPreparedUpdateService dropPreparedUpdateService;

    @Mock
    private SpotMembershipService spotMembershipService;

    @Mock
    private NotificationService notificationService;

    @Test
    void givenDropSpotAndRequestWhenUpdateThenUpdateAndSendNotifications() {
        //given
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusMinutes(15)).build();
        final Spot spot = Spot.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final Company company = Company.builder().build();

        doNothing().when(notificationService).createNotifications(any());
        when(spotMembershipService.findToBeNotified(spot, SpotMembershipNotificationStatus.prepared())).thenReturn(List.of());

        //when
        final DropStatus result = dropPreparedUpdateService.update(drop, spot, company, accountProfile);

        //then
        assertThat(result).isEqualTo(DropStatus.PREPARED);
        assertThat(drop.getPreparedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }
}