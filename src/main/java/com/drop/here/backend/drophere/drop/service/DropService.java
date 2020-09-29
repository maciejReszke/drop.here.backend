package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.drop.dto.DropCustomerSpotResponse;
import com.drop.here.backend.drophere.drop.dto.DropDetailedCustomerResponse;
import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.service.RouteProductMappingService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotMappingService;
import com.drop.here.backend.drophere.spot.service.SpotPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropService {
    private final DropRepository dropRepository;
    private final SpotMappingService spotMappingService;
    private final RouteProductMappingService routeProductMappingService;
    private final SpotPersistenceService spotPersistenceService;
    private final AccountProfilePersistenceService accountProfilePersistenceService;

    public DropDetailedCustomerResponse findDrop(String dropUid, AccountAuthentication authentication) {
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
                .id(drop.getId())
                .spot(spotMappingService.toSpotCompanyResponse(drop.getSpot()))
                .build();
    }

    public List<DropCustomerSpotResponse> findDrops(Spot spot, LocalDateTime from, LocalDateTime to) {
        return dropRepository.findBySpotAndStartTimeAfterAndStartTimeBefore(spot, from, to)
                .stream()
                .map(this::toDropCustomerSpotResponse)
                .collect(Collectors.toList());
    }

    private DropCustomerSpotResponse toDropCustomerSpotResponse(Drop drop) {
        return DropCustomerSpotResponse
                .builder()
                .description(drop.getDescription())
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .name(drop.getName())
                .status(drop.getStatus())
                .uid(drop.getUid())
                .build();
    }
}
