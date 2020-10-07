package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.dto.RouteStatusChange;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class RouteUpdateStateServiceFactory {
    private final RouteLiveUpdateStateService routeLiveUpdateStateService;
    private final RouteCancelUpdateStateService routeCancelUpdateStateService;
    private final RoutePreparedUpdateStateService routePreparedUpdateStateService;

    // TODO: 07/10/2020 test, implement
    public RouteStatus update(Route route, RouteStateChangeRequest request) {
        return API.Match(request.getNewStatus()).of(
                Case($(RouteStatusChange.LIVE), () -> routeLiveUpdateStateService),
                Case($(RouteStatusChange.PREPARED), () -> routePreparedUpdateStateService),
                Case($(RouteStatusChange.CANCELLED), () -> routeCancelUpdateStateService))
                .update(route, request);
    }
}
