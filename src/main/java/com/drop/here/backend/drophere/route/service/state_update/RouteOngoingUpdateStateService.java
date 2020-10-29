package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.service.RouteValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RouteOngoingUpdateStateService implements RouteUpdateStateService {
    private final RouteValidationService routeValidationService;

    @Override
    public RouteStatus update(Route route) {
        routeValidationService.validateOngoingUpdate(route);
        route.setOngoingAt(LocalDateTime.now());
        return RouteStatus.ONGOING;
    }
}
