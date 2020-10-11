package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final RouteMappingService routeMappingService;
    private final RoutePersistenceService routePersistenceService;
    private final RouteValidationService routeValidationService;
    private final RouteUpdateStateServiceFactory routeUpdateStateServiceFactory;
    private final AccountProfilePersistenceService accountProfilePersistenceService;

    public Page<RouteShortResponse> findRoutes(AccountAuthentication accountAuthentication, String routeStatus, Pageable pageable) {
        return routePersistenceService.findByCompany(accountAuthentication.getCompany(), routeStatus, pageable);
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
        routePersistenceService.save(route);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, route.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse updateUnpreparedRoute(String companyUid, Long routeId, UnpreparedRouteRequest routeRequest, AccountAuthentication accountAuthentication) {
        final Route route = findByIdAndCompany(routeId, accountAuthentication.getCompany());
        routeValidationService.validateUpdateUnprepared(routeRequest, route);
        routeMappingService.updateRoute(route, routeRequest, accountAuthentication.getCompany());
        log.info("Updating route for company {} with name {} id {} {}", companyUid, route.getName(), route.getId(), route.getStatus());
        routePersistenceService.save(route);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, route.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse updateRouteStatus(String companyUid, Long routeId, RouteStateChangeRequest routeStateChangeRequest, AccountAuthentication accountAuthentication) {
        final Route route = findByIdAndCompany(routeId, accountAuthentication.getCompany());
        routeValidationService.validateUpdateStateChanged(route, accountAuthentication.getProfile());
        final AccountProfile newSeller = getNewSeller(companyUid, route, routeStateChangeRequest, accountAuthentication);
        route.setWithSeller(newSeller != null);
        route.setProfile(newSeller);
        final RouteStatus newStatus = routeUpdateStateServiceFactory.update(route, routeStateChangeRequest);
        log.info("Updating route for company {} with name {} id {} from {} to {}", companyUid, route.getName(), route.getId(), route.getStatus(), newStatus);
        route.setStatus(newStatus);
        routePersistenceService.save(route);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, route.getId());
    }

    private AccountProfile getNewSeller(String companyUid, Route route, RouteStateChangeRequest routeStateChangeRequest, AccountAuthentication accountAuthentication) {
        return StringUtils.isBlank(routeStateChangeRequest.getChangedProfileUid())
                ? route.getProfile()
                : accountProfilePersistenceService.findActiveByCompanyAndProfileUid(accountAuthentication.getCompany(), routeStateChangeRequest.getChangedProfileUid())
                .orElseThrow(() -> new RestEntityNotFoundException(String.format("Profile with uid %s for company %s was not found",
                        routeStateChangeRequest.getChangedProfileUid(), companyUid),
                        RestExceptionStatusCode.UPDATE_ROUTE_STATUS_SELLER_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse deleteRoute(String companyUid, Long routeId, AccountAuthentication accountAuthentication) {
        final Route route = findByIdAndCompany(routeId, accountAuthentication.getCompany());
        routeValidationService.validateDelete(route);
        log.info("Deleting route for company {} with name {} id {}", companyUid, route.getName(), routeId);
        routePersistenceService.delete(route);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, routeId);
    }

    private Route findByIdAndCompany(Long routeId, Company company) {
        return routePersistenceService.findByIdAndCompany(routeId, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Route for company %s with id %s was not found", company.getUid(), routeId),
                        RestExceptionStatusCode.ROUTE_BY_ID_AND_COMPANY_NOT_FOUND
                ));
    }

    public boolean shouldBeStreamingPosition(AccountProfile profile) {
        return routePersistenceService.existsByStatusAndProfile(RouteStatus.ONGOING, profile);
    }
}
