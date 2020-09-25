package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.entity.Drop;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// TODO: 25/09/2020 sprawdzic czy po updacie czyscza sie dropy stare (i stare produkty)
@Service
public class RouteMappingService {

    // TODO: 25/09/2020 test
    public Route toRoute(RouteRequest routeRequest, Company company) {
        final Route route = Route.builder()
                .createdAt(LocalDateTime.now())
                .status(RouteStatus.PREPARED)
                .company(company)
                .build();
        updateRoute(route, routeRequest, company);
        return route;
    }

    // TODO: 25/09/2020 test
    public void updateRoute(Route route, RouteRequest routeRequest, Company company) {
        CollectionUtils.emptyIfNull(route.getProducts()).forEach(product -> product.setRoute(null));
        CollectionUtils.emptyIfNull(route.getDrops()).forEach(drop -> drop.setRoute(null));
        route.setLastUpdatedAt(LocalDateTime.now());
        route.setName(routeRequest.getName().strip());
        route.setDescription(routeRequest.getDescription());
        route.setRouteDate(LocalDate.parse(routeRequest.getDate().strip(), DateTimeFormatter.ISO_LOCAL_DATE));
        route.setProducts(buildProducts(routeRequest, route, company));
        route.setDrops(buildDrops(routeRequest, route, company));
        route.setProfile(getProfile(routeRequest));
    }

    // TODO: 25/09/2020
    private AccountProfile getProfile(RouteRequest routeRequest) {
        return null;
    }

    // TODO: 25/09/2020
    private List<Drop> buildDrops(RouteRequest routeRequest, Route route, Company company) {
        return null;
    }

    // TODO: 25/09/2020
    private List<RouteProduct> buildProducts(RouteRequest routeRequest, Route route, Company company) {
        return null;
    }

    // TODO: 25/09/2020  test ,implement(pamietac o relacjach)
    public RouteResponse toRouteResponse(Route route) {
        return null;
    }
}
