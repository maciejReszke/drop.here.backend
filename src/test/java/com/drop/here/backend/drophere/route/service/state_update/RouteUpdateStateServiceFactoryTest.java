package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.dto.RouteStatusChange;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteUpdateStateServiceFactoryTest {

    @InjectMocks
    private RouteUpdateStateServiceFactory routeUpdateStateServiceFactory;

    @Mock
    private RouteOngoingUpdateStateService routeOngoingUpdateStateService;

    @Mock
    private RouteCancelUpdateStateService routeCancelUpdateStateService;

    @Mock
    private RoutePreparedUpdateStateService routePreparedUpdateStateService;

    @Test
    void givenOngoingUpdateWhenUpdateThenUpdate() {
        //given
        final Route route = Route.builder().build();
        final RouteStateChangeRequest routeStateChangeRequest = RouteStateChangeRequest.builder()
                .newStatus(RouteStatusChange.ONGOING)
                .build();

        when(routeOngoingUpdateStateService.update(route)).thenReturn(RouteStatus.ONGOING);
        //when
        final RouteStatus result = routeUpdateStateServiceFactory.update(route, routeStateChangeRequest);

        //then
        assertThat(result).isEqualTo(RouteStatus.ONGOING);
    }

    @Test
    void givenCancelledUpdateWhenUpdateThenUpdate() {
        //given
        final Route route = Route.builder().build();
        final RouteStateChangeRequest routeStateChangeRequest = RouteStateChangeRequest.builder()
                .newStatus(RouteStatusChange.CANCELLED)
                .build();

        when(routeCancelUpdateStateService.update(route)).thenReturn(RouteStatus.ONGOING);
        //when
        final RouteStatus result = routeUpdateStateServiceFactory.update(route, routeStateChangeRequest);

        //then
        assertThat(result).isEqualTo(RouteStatus.ONGOING);
    }

    @Test
    void givenPreparedUpdateWhenUpdateThenUpdate() {
        //given
        final Route route = Route.builder().build();
        final RouteStateChangeRequest routeStateChangeRequest = RouteStateChangeRequest.builder()
                .newStatus(RouteStatusChange.PREPARED)
                .build();

        when(routePreparedUpdateStateService.update(route)).thenReturn(RouteStatus.ONGOING);
        //when
        final RouteStatus result = routeUpdateStateServiceFactory.update(route, routeStateChangeRequest);

        //then
        assertThat(result).isEqualTo(RouteStatus.ONGOING);
    }

}