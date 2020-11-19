package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.DropStatusChange;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropUpdateServiceFactoryTest {
    @InjectMocks
    private DropUpdateServiceFactory dropUpdateServiceFactory;

    @Mock
    private DropLiveUpdateService dropLiveUpdateService;

    @Mock
    private DropCancelledUpdateService dropCancelledUpdateService;

    @Mock
    private DropDelayedUpdateService dropDelayedUpdateService;

    @Mock
    private DropFinishedUpdateService dropFinishedUpdateService;

    @Mock
    private DropPreparedUpdateService dropPreparedUpdateService;

    @Test
    void givenDelayedUpdateWhenUpdateThenUpdate() {
        //given
        final Drop drop = Drop.builder().build();
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .newStatus(DropStatusChange.DELAYED)
                .build();
        final Spot spot = Spot.builder().build();
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();

        when(dropDelayedUpdateService.update(drop, spot, company, accountProfile, dropManagementRequest, false)).thenReturn(DropStatus.DELAYED);

        //when
        final DropStatus update = dropUpdateServiceFactory.update(drop, spot, company, accountProfile, dropManagementRequest, false);

        //then
        assertThat(update).isEqualTo(DropStatus.DELAYED);
    }

    @Test
    void givenLiveUpdateWhenUpdateThenUpdate() {
        //given
        final Drop drop = Drop.builder().build();
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .newStatus(DropStatusChange.LIVE)
                .build();
        final Spot spot = Spot.builder().build();
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        when(dropLiveUpdateService.update(drop, spot, company, accountProfile, dropManagementRequest, false)).thenReturn(DropStatus.DELAYED);

        //when
        final DropStatus update = dropUpdateServiceFactory.update(drop, spot, company, accountProfile, dropManagementRequest, false);

        //then
        assertThat(update).isEqualTo(DropStatus.DELAYED);
    }

    @Test
    void givenFinishedUpdateWhenUpdateThenUpdate() {
        //given
        final Drop drop = Drop.builder().build();
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .newStatus(DropStatusChange.FINISHED)
                .build();
        final Spot spot = Spot.builder().build();
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();

        when(dropFinishedUpdateService.update(drop, spot, company, accountProfile, dropManagementRequest, false)).thenReturn(DropStatus.DELAYED);

        //when
        final DropStatus update = dropUpdateServiceFactory.update(drop, spot, company, accountProfile, dropManagementRequest, false);

        //then
        assertThat(update).isEqualTo(DropStatus.DELAYED);
    }

    @Test
    void givenCancelledUpdateWhenUpdateThenUpdate() {
        //given
        final Drop drop = Drop.builder().build();
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .newStatus(DropStatusChange.CANCELLED)
                .build();
        final Spot spot = Spot.builder().build();
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        when(dropCancelledUpdateService.update(drop, spot, company, accountProfile, dropManagementRequest, false)).thenReturn(DropStatus.DELAYED);

        //when
        final DropStatus update = dropUpdateServiceFactory.update(drop, spot, company, accountProfile, dropManagementRequest, false);

        //then
        assertThat(update).isEqualTo(DropStatus.DELAYED);
    }

    @Test
    void whenPrepareThenPrepare() {
        //given
        final Drop drop = Drop.builder().build();
        final Spot spot = Spot.builder().build();
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        when(dropPreparedUpdateService.update(drop, spot, company, accountProfile)).thenReturn(DropStatus.DELAYED);

        //when
        final DropStatus result = dropUpdateServiceFactory.prepare(drop, spot, company, accountProfile);

        //then
        assertThat(result).isEqualTo(DropStatus.DELAYED);
    }
}