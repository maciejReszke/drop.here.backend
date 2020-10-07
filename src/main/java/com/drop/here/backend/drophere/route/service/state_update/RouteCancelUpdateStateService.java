package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import org.springframework.stereotype.Service;

@Service
public class RouteCancelUpdateStateService implements RouteUpdateStateService {
    // TODO: 07/10/2020
    @Override
    public RouteStatus update(Route route, RouteStateChangeRequest request) {
        return null;

    }
}
