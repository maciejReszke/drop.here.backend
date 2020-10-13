package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropDetailedCustomerResponse;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.dto.DropStatusChange;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.drop.service.update.DropUpdateServiceFactory;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.service.RouteProductMappingService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotMappingService;
import com.drop.here.backend.drophere.spot.service.SpotPersistenceService;
import com.drop.here.backend.drophere.spot.service.SpotSearchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DropService {
    private final DropRepository dropRepository;
    private final SpotMappingService spotMappingService;
    private final RouteProductMappingService routeProductMappingService;
    private final SpotPersistenceService spotPersistenceService;
    private final SpotSearchingService spotSearchingService;
    private final AccountProfilePersistenceService accountProfilePersistenceService;
    private final DropValidationService dropValidationService;
    private final DropUpdateServiceFactory dropUpdateServiceFactory;

    public DropDetailedCustomerResponse findDropForCustomer(String dropUid, AccountAuthentication authentication) {
        final Customer customer = authentication.getCustomer();
        final Drop drop = findPrivilegedDrop(dropUid, customer);
        final Spot spot = spotPersistenceService.findByIdWithCompany(drop.getSpot().getId());
        return toDropCustomerDetailedResponse(drop, spot, customer);
    }

    public Drop findPrivilegedDrop(String dropUid, Customer customer) {
        return dropRepository.findPrivilegedDrop(dropUid, customer)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with uid %s was not found or is not privileged", dropUid),
                        RestExceptionStatusCode.PRIVILEGED_FOR_CUSTOMER_DROP_NOT_FOUND));
    }

    private DropDetailedCustomerResponse toDropCustomerDetailedResponse(Drop drop, Spot spot, Customer customer) {
        final Optional<AccountProfile> profile = accountProfilePersistenceService.findByDrop(drop);
        return DropDetailedCustomerResponse.builder()
                .uid(drop.getUid())
                .name(drop.getName())
                .description(drop.getDescription())
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(drop.getStatus())
                .spot(spotSearchingService.findSpot(spot, customer))
                .products(routeProductMappingService.toProductResponses(drop))
                .profileUid(profile.map(AccountProfile::getProfileUid).orElse(null))
                .profileFirstName(profile.map(AccountProfile::getFirstName).orElse(null))
                .profileLastName(profile.map(AccountProfile::getLastName).orElse(null))
                .streamingPosition(profile.map(AccountProfile::getProfileUid)
                        .map(uid -> isSellerLocationAvailableForCustomer(uid, customer))
                        .orElse(false))
                .build();
    }

    public List<DropRouteResponse> toDropRouteResponses(Route route) {
        return dropRepository.findByRouteWithSpot(route)
                .stream()
                .map(this::toDropRouteResponse)
                .collect(Collectors.toList());
    }

    private DropRouteResponse toDropRouteResponse(Drop drop) {
        return DropRouteResponse
                .builder()
                .description(drop.getDescription())
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .name(drop.getName())
                .status(drop.getStatus())
                .uid(drop.getUid())
                .spot(spotMappingService.toSpotCompanyResponse(drop.getSpot()))
                .build();
    }

    public boolean isSellerLocationAvailableForCustomer(String profileUid, Customer customer) {
        return dropRepository.isSellerLocationAvailableForCustomer(profileUid, customer);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse updateDrop(DropManagementRequest dropManagementRequest, String dropUid, AccountAuthentication authentication) {
        final Company company = authentication.getCompany();
        final Drop drop = findDrop(dropUid, company);
        dropValidationService.validateUpdate(drop, authentication.getProfile());
        final DropStatus newStatus = dropUpdateServiceFactory.update(drop, drop.getSpot(), company, authentication.getProfile(), dropManagementRequest);
        updateStatus(drop, newStatus);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, drop.getId());
    }

    private void updateStatus(Drop drop, DropStatus newStatus) {
        final DropStatus preUpdateStatus = drop.getStatus();
        drop.setStatus(newStatus);
        dropRepository.save(drop);
        log.info("Successfully updated drop {} from status {} to {}", drop.getUid(), preUpdateStatus, newStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    public void prepareDrops(Route route) {
        final Company company = route.getCompany();
        final List<Drop> drops = dropRepository.findByRouteWithSpot(route);
        drops.forEach(drop -> {
            final DropStatus newStatus = dropUpdateServiceFactory.prepare(drop, drop.getSpot(), company, route.getProfile());
            updateStatus(drop, newStatus);
        });
    }


    private Drop findDrop(String dropUid, Company company) {
        return dropRepository.findByUidAndRouteCompanyWithSpot(dropUid, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with uid %s for company %s was not found", dropUid, company.getUid()),
                        RestExceptionStatusCode.DROP_BY_UID_FOR_COMPANY_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelDrops(Route route) {
        final Company company = route.getCompany();
        final List<Drop> drops = dropRepository.findByRouteWithSpot(route);
        drops.stream()
                .filter(drop -> drop.getStatus() != DropStatus.CANCELLED && drop.getStatus() != DropStatus.FINISHED)
                .forEach(drop -> {
                    final DropManagementRequest cancelRequest = DropManagementRequest.builder()
                            .newStatus(DropStatusChange.CANCELLED)
                            .build();
                    final DropStatus newStatus = dropUpdateServiceFactory.update(drop, drop.getSpot(), company, route.getProfile(), cancelRequest);
                    updateStatus(drop, newStatus);
                });
    }
}
