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
    private final AccountProfilePersistenceService accountProfilePersistenceService;
    private final DropValidationService dropValidationService;
    private final DropUpdateServiceFactory dropUpdateServiceFactory;

    public DropDetailedCustomerResponse findDropForCustomer(String dropUid, AccountAuthentication authentication) {
        final Drop drop = dropRepository.findPrivilegedDrop(dropUid, authentication.getCustomer())
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with uid %s was not found or is not privileged", dropUid),
                        RestExceptionStatusCode.PRIVILEGED_FOR_CUSTOMER_DROP_NOT_FOUND));
        final Spot spot = spotPersistenceService.findByIdWithCompany(drop.getSpot().getId());
        return toDropCustomerDetailedResponse(drop, spot);
    }

    private DropDetailedCustomerResponse toDropCustomerDetailedResponse(Drop drop, Spot spot) {
        final Optional<AccountProfile> profile = accountProfilePersistenceService.findByDrop(drop);
        return DropDetailedCustomerResponse.builder()
                .uid(drop.getUid())
                .name(drop.getName())
                .description(drop.getDescription())
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(drop.getStatus())
                .spot(spotMappingService.toMembershipSpotBaseCustomerResponse(spot))
                .products(routeProductMappingService.toProductResponses(drop))
                .profileUid(profile.map(AccountProfile::getProfileUid).orElse(null))
                .profileFirstName(profile.map(AccountProfile::getFirstName).orElse(null))
                .profileLastName(profile.map(AccountProfile::getLastName).orElse(null))
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
        final DropStatus preUpdateStatus = drop.getStatus();
        final DropStatus newStatus = dropUpdateServiceFactory.update(drop, drop.getSpot(), company, dropManagementRequest);
        drop.setStatus(newStatus);
        dropRepository.save(drop);
        log.info("Successfully updated drop {} from status {} to {}", drop.getUid(), preUpdateStatus, newStatus);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, drop.getId());
    }

    private Drop findDrop(String dropUid, Company company) {
        return dropRepository.findByUidAndRouteCompanyWithSpot(dropUid, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with uid %s for company %s was not found", dropUid, company.getUid()),
                        RestExceptionStatusCode.DROP_BY_UID_FOR_COMPANY_NOT_FOUND));
    }
}
