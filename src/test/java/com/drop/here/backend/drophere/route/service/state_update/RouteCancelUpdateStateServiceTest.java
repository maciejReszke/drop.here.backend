package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.service.RouteValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class RouteCancelUpdateStateServiceTest {

    @InjectMocks
    private RouteCancelUpdateStateService routeCancelUpdateStateService;

    @Mock
    private DropService dropService;

    @Mock
    private RouteValidationService routeValidationService;

    @Test
    void givenRouteWhenUpdateThenUpdate() {
        //given
        final Route route = Route.builder().build();

        doNothing().when(routeValidationService).validateCancelUpdate(route);
        doNothing().when(dropService).cancelDrops(route);

        //when
        final RouteStatus status = routeCancelUpdateStateService.update(route);

        //then
        assertThat(status).isEqualTo(RouteStatus.CANCELLED);
        assertThat(route.getCanceledAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }
}