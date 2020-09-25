package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCustomerResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotMembershipService {
    private final SpotPersistenceService spotPersistenceService;
    private final SpotMappingService spotMappingService;
    private final SpotMembershipRepository spotMembershipRepository;
    private final SpotManagementValidationService spotManagementValidationService;
    private final SpotMembershipSearchingService spotMembershipSearchingService;
    private final SpotSearchingService spotSearchingService;

    public Flux<SpotCompanyMembershipResponse> findMemberships(Spot spot, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        return spotMembershipSearchingService.findMemberships(spot, desiredCustomerSubstring, membershipStatus, pageable);
    }

    public Mono<ResourceOperationResponse> createSpotMembership(SpotJoinRequest spotJoinRequest, String spotUid, String companyUid, AccountAuthentication authentication) {
        return spotPersistenceService.findSpot(spotUid, companyUid)
                .flatMap(spot -> spotManagementValidationService.validateJoinSpotRequest(spot, spotJoinRequest, authentication.getCustomer())
                        .thenReturn(spotMappingService.createMembership(spot, spotJoinRequest, authentication))
                        .doOnNext(spotMembership -> log.info("Creating new spot membership for spot {} customer {}", spot.getUid(), authentication.getCustomer().getId())))
                .flatMap(spotMembershipRepository::save)
                .map(spotMembership -> new ResourceOperationResponse(ResourceOperationStatus.CREATED, spotMembership.getId()));
    }

    public Mono<ResourceOperationResponse> deleteSpotMembership(String spotUid, String companyUid, AccountAuthentication authentication) {
        return spotPersistenceService.findSpot(spotUid, companyUid)
                .flatMap(spot -> getSpotMembership(spot, authentication))
                .doOnNext(spotManagementValidationService::validateDeleteSpotMembership)
                .doOnNext(spotMembership -> log.info("Deleting spot membership for spot {} customer {}", spotUid, authentication.getCustomer().getId()))
                .flatMap(spotMembership -> spotMembershipRepository.delete(spotMembership)
                        .thenReturn(new ResourceOperationResponse(ResourceOperationStatus.DELETED, spotMembership.getId())));
    }

    private Mono<SpotMembership> getSpotMembership(Spot spot, AccountAuthentication authentication) {
        return spotMembershipRepository.findBySpotAndCustomer(spot, authentication.getCustomer())
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Spot membership for customer %s spot %s was not found", authentication.getCustomer().getId(), spot.getId()),
                        RestExceptionStatusCode.DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND
                )));
    }

    private Mono<SpotMembership> getSpotMembership(Spot spot, Long membershipId) {
        return spotMembershipRepository.findByIdAndSpot(membershipId, spot)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Spot membership with id %s spot %s was not found", membershipId, spot.getId()),
                        RestExceptionStatusCode.DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND
                )));
    }

    public Mono<Void> deleteMemberships(Spot spot) {
        return spotMembershipRepository.deleteBySpot(spot);
    }

    public Mono<ResourceOperationResponse> updateMembership(Spot spot, Long membershipId, SpotCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        spotManagementValidationService.validateUpdateMembership(companyMembershipManagementRequest);
        return getSpotMembership(spot, membershipId)
                .doOnNext(spotMembership -> {
                    spotMembership.setMembershipStatus(SpotMembershipStatus.valueOf(companyMembershipManagementRequest.getMembershipStatus()));
                    spotMembership.setLastUpdatedAt(LocalDateTime.now());
                })
                .flatMap(spotMembershipRepository::save)
                .doOnNext(spotMembership -> log.info("Updating membership {} status to {}", spotMembership.getId(), companyMembershipManagementRequest.getMembershipStatus()))
                .map(spotMembership -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, spotMembership.getId()));
    }

    public Mono<Boolean> existsMembership(Company company, String customerId) {
        return spotMembershipRepository.existsBySpotCompanyAndCustomerId(company, customerId)
                .map(ignore -> true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<ResourceOperationResponse> updateSpotMembership(SpotMembershipManagementRequest spotMembershipManagementRequest, String spotUid, String companyUid, AccountAuthentication authentication) {
        return spotPersistenceService.findSpot(spotUid, companyUid)
                .flatMap(spot -> getSpotMembership(spot, authentication)
                        .doOnNext(spotMembership -> {
                            spotMembership.setReceiveNotification(spotMembershipManagementRequest.isReceiveNotification());
                            log.info("Updating spot membership for spot {} customer {}", spot.getUid(), authentication.getCustomer().getId());

                        }))
                .flatMap(spotMembershipRepository::save)
                .map(spotMembership -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, spotMembership.getId()));
    }

    public Flux<SpotMembership> findMembershipsJoinFetchSpots(List<Long> customersIds, Company company) {
        return spotMembershipRepository.findBySpotCompanyAndCustomerIdInJoinFetchSpots(company, customersIds);
    }

    public Flux<SpotCustomerResponse> findSpots(AccountAuthentication authentication, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Pageable pageable) {
        return spotSearchingService.findSpots(authentication, xCoordinate, yCoordinate, radius, member, namePrefix, pageable);
    }
}
