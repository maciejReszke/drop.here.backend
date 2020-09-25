package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotManagementService {
    private final SpotManagementValidationService spotManagementValidationService;
    private final SpotMappingService spotMappingService;
    private final SpotRepository spotRepository;
    private final SpotMembershipService spotMembershipService;

    public Flux<SpotCompanyResponse> findCompanySpots(String companyUid, String name) {
        return spotRepository.findAllByCompanyUidAndNameStartsWith(companyUid, StringUtils.defaultIfEmpty(name, ""))
                .map(spotMappingService::toSpotCompanyResponse);
    }

    public Mono<ResourceOperationResponse> createSpot(SpotManagementRequest spotManagementRequest, String companyUid, AccountAuthentication authentication) {
        spotManagementValidationService.validateSpotRequest(spotManagementRequest);
        final Spot spot = spotMappingService.toEntity(spotManagementRequest, authentication);
        log.info("Creating spot for company {} with uid {}", companyUid, spot.getUid());
        return spotRepository.save(spot)
                .map(saved -> new ResourceOperationResponse(ResourceOperationStatus.CREATED, saved.getId()));
    }

    public Mono<ResourceOperationResponse> updateSpot(SpotManagementRequest spotManagementRequest, String spotId, String companyUid) {
        return getSpot(spotId, companyUid)
                .doOnNext(spot -> spotManagementValidationService.validateSpotRequest(spotManagementRequest))
                .doOnNext(spot -> spotMappingService.update(spot, spotManagementRequest))
                .doOnNext(spot -> log.info("Updating spot for company {} with uid {}", companyUid, spot.getUid()))
                .flatMap(spotRepository::save)
                .map(spot -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, spot.getId()));
    }

    private Mono<Spot> getSpot(String spotId, String companyUid) {
        return spotRepository.findByIdAndCompanyUid(spotId, companyUid)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Spot with id %s company %s was not found", spotId, companyUid),
                        RestExceptionStatusCode.SPOT_NOT_FOUND_BY_ID)));
    }

    public Mono<ResourceOperationResponse> deleteSpot(String spotId, String companyUid) {
        return getSpot(spotId, companyUid)
                .doOnNext(spot -> log.info("Deleting spot for company {} with uid {}", companyUid, spot.getUid()))
                .flatMap(spotMembershipService::deleteMemberships)
                .thenReturn(new ResourceOperationResponse(ResourceOperationStatus.DELETED, spotId));
    }

    public Flux<SpotCompanyMembershipResponse> findMemberships(String spotId, String companyUid, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        return getSpot(spotId, companyUid)
                .flatMapMany(spot -> spotMembershipService.findMemberships(spot, desiredCustomerSubstring, membershipStatus, pageable));
    }

    public Mono<ResourceOperationResponse> updateMembership(String spotId, String companyUid, Long membershipId, SpotCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        return getSpot(spotId, companyUid)
                .flatMap(spot -> spotMembershipService.updateMembership(spot, membershipId, companyMembershipManagementRequest));
    }
}
