package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.dto.RouteShortResponse;
import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.dto.UnpreparedRouteRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.service.state_update.RouteUpdateStateServiceFactory;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final RouteMappingService routeMappingService;
    private final RouteStoreService routeStoreService;
    private final RouteValidationService routeValidationService;
    private final RouteUpdateStateServiceFactory routeUpdateStateServiceFactory;

    public Page<RouteShortResponse> findRoutes(AccountAuthentication accountAuthentication, String routeStatus, Pageable pageable) {
        return routeStoreService.findByCompany(accountAuthentication.getCompany(), routeStatus, pageable);
    }

    public RouteResponse findRoute(Long routeId, AccountAuthentication accountAuthentication) {
        final Route route = findByIdAndCompany(routeId, accountAuthentication.getCompany());
        return routeMappingService.toRouteResponse(route);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse createRoute(String companyUid, UnpreparedRouteRequest unpreparedRouteRequest, AccountAuthentication accountAuthentication) {
        routeValidationService.validateCreate(unpreparedRouteRequest);
        final Route route = routeMappingService.toRoute(unpreparedRouteRequest, accountAuthentication.getCompany());
        log.info("Saving route for company {} with name {}", companyUid, unpreparedRouteRequest.getName());
        routeStoreService.save(route);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, route.getId());
    }

    // TODO: 07/10/2020 test
    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse updateRoute(String companyUid, Long routeId, RouteRequest routeRequest, AccountAuthentication accountAuthentication) {
        final Route route = findByIdAndCompany(routeId, accountAuthentication.getCompany());
        return routeRequest.getRouteStateChangeRequest() == null
                ? updateRouteUnprepared(companyUid, route, routeRequest.getUnpreparedRouteRequest(), accountAuthentication)
                : updateRouteStatus(companyUid, route, routeRequest.getRouteStateChangeRequest(), accountAuthentication);
    }

    private ResourceOperationResponse updateRouteUnprepared(String companyUid, Route route, UnpreparedRouteRequest routeRequest, AccountAuthentication accountAuthentication) {
        routeValidationService.validateUpdateUnprepared(routeRequest, route);
        routeMappingService.updateRoute(route, routeRequest, accountAuthentication.getCompany());
        log.info("Updating route for company {} with name {} id {} {}", companyUid, route.getName(), route.getId(), route.getStatus());
        routeStoreService.save(route);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, route.getId());
    }

    // TODO: 07/10/2020 - sprawdzic czy account profile moze tego dokonac + update profilu
    private ResourceOperationResponse updateRouteStatus(String companyUid, Route route, RouteStateChangeRequest routeStateChangeRequest, AccountAuthentication accountAuthentication) {
        final RouteStatus newStatus = routeUpdateStateServiceFactory.update(route, routeStateChangeRequest);
        log.info("Updating route for company {} with name {} id {} from {} to {}", companyUid, route.getName(), route.getId(), route.getStatus(), newStatus);
        route.setStatus(newStatus);
        routeStoreService.save(route);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, route.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse deleteRoute(String companyUid, Long routeId, AccountAuthentication accountAuthentication) {
        final Route route = findByIdAndCompany(routeId, accountAuthentication.getCompany());
        routeValidationService.validateDelete(route);
        log.info("Deleting route for company {} with name {} id {}", companyUid, route.getName(), routeId);
        routeStoreService.delete(route);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, routeId);
    }

    private Route findByIdAndCompany(Long routeId, Company company) {
        return routeStoreService.findByIdAndCompany(routeId, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Route for company %s with id %s was not found", company.getUid(), routeId),
                        RestExceptionStatusCode.ROUTE_BY_ID_AND_COMPANY_NOT_FOUND
                ));
    }

}
