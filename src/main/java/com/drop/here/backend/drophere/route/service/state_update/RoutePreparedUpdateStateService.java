package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.service.RouteValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RoutePreparedUpdateStateService implements RouteUpdateStateService {
    private final DropService dropService;
    private final RouteValidationService routeValidationService;

    @Override
    public RouteStatus update(Route route) {
        routeValidationService.validatePreparedUpdate(route);
        dropService.prepareDrops(route);
        route.setPreparedAt(LocalDateTime.now());
        return RouteStatus.PREPARED;
    }
}
