package com.drop.here.backend.drophere.route.service.state_update;

import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;

public interface RouteUpdateStateService {
    RouteStatus update(Route route);
}
