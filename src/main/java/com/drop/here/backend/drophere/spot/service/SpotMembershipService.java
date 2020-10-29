package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.SpotMembershipNotificationStatus;
import com.drop.here.backend.drophere.spot.dto.request.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotBaseCustomerResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotDetailedCustomerResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NotificationService notificationService;

    public List<SpotMembership> findToBeNotified(Spot spot, SpotMembershipNotificationStatus notificationStatus) {
        return spotMembershipRepository.findToBeNotified(
                spot, notificationStatus.isPrepared(),
                notificationStatus.isLive(),
                notificationStatus.isFinished(),
                notificationStatus.isDelayed(),
                notificationStatus.isCanceled());
    }

    public Page<SpotCompanyMembershipResponse> findMemberships(Spot spot, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        return spotMembershipSearchingService.findMemberships(spot, desiredCustomerSubstring, membershipStatus, pageable);
    }

    public ResourceOperationResponse createSpotMembership(SpotJoinRequest spotJoinRequest, String spotUid, String companyUid, AccountAuthentication authentication) {
        final Spot spot = spotPersistenceService.findSpot(spotUid, companyUid);
        spotManagementValidationService.validateJoinSpotRequest(spot, spotJoinRequest, authentication.getCustomer());
        final SpotMembership membership = spotMappingService.createMembership(spot, spotJoinRequest, authentication);
        log.info("Creating new spot membership for spot {} customer {}", spot.getUid(), authentication.getCustomer().getId());
        spotMembershipRepository.save(membership);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, membership.getId());
    }

    public ResourceOperationResponse deleteSpotMembership(String spotUid, String companyUid, AccountAuthentication authentication) {
        final Spot spot = spotPersistenceService.findSpot(spotUid, companyUid);
        final SpotMembership spotMembership = getSpotMembership(spot, authentication);
        spotManagementValidationService.validateDeleteSpotMembership(spotMembership);
        log.info("Deleting spot membership for spot {} customer {}", spot.getUid(), authentication.getCustomer().getId());
        spotMembershipRepository.delete(spotMembership);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, spotMembership.getId());
    }

    private SpotMembership getSpotMembership(Spot spot, AccountAuthentication authentication) {
        return spotMembershipRepository.findBySpotAndCustomer(spot, authentication.getCustomer())
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Spot membership for customer %s spot %s was not found", authentication.getCustomer().getId(), spot.getId()),
                        RestExceptionStatusCode.DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND
                ));
    }

    private SpotMembership getSpotMembership(Spot spot, Long membershipId) {
        return spotMembershipRepository.findByIdAndSpot(membershipId, spot)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Spot membership with id %s spot %s was not found", membershipId, spot.getId()),
                        RestExceptionStatusCode.DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND
                ));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMemberships(Spot spot) {
        spotMembershipRepository.deleteBySpot(spot);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse updateMembership(Spot spot, Long membershipId, SpotCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        spotManagementValidationService.validateUpdateMembership(companyMembershipManagementRequest);
        final SpotMembership spotMembership = getSpotMembership(spot, membershipId);
        final SpotMembershipStatus newMembershipStatus = SpotMembershipStatus.valueOf(companyMembershipManagementRequest.getMembershipStatus());
        notifyPendingAccepted(spotMembership, newMembershipStatus, spot);
        spotMembership.setMembershipStatus(newMembershipStatus);
        spotMembership.setLastUpdatedAt(LocalDateTime.now());
        spotMembershipRepository.save(spotMembership);
        log.info("Updating membership {} status to {}", spotMembership.getId(), companyMembershipManagementRequest.getMembershipStatus());
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, spotMembership.getId());
    }

    private void notifyPendingAccepted(SpotMembership spotMembership, SpotMembershipStatus newMembershipStatus, Spot spot) {
        if (newMembershipStatus == SpotMembershipStatus.ACTIVE && spotMembership.getMembershipStatus() == SpotMembershipStatus.PENDING) {
            notificationService.createNotifications(NotificationCreationRequest.builder()
                    .broadcastingCompany(spot.getCompany())
                    .broadcastingType(NotificationBroadcastingType.COMPANY)
                    .message(String.format("Your join request to spot %s was accepted!", spot.getName()))
                    .notificationCategory(NotificationCategory.SPOT_JOIN_REQUEST_ACCEPTED)
                    .notificationType(NotificationType.PUSH_NOTIFICATION_ONLY)
                    .recipientCustomers(List.of(spotMembership.getCustomer()))
                    .referencedSubjectId(spot.getUid())
                    .referencedSubjectType(NotificationReferencedSubjectType.SPOT)
                    .title(String.format("%s joined!", spot.getName()))
                    .build()
            );
        }
    }

    public boolean existsMembership(Company company, Long customerId) {
        return spotMembershipRepository.existsBySpotCompanyAndCustomerId(company, customerId);
    }

    public ResourceOperationResponse updateSpotMembership(SpotMembershipManagementRequest spotMembershipManagementRequest, String spotUid, String companyUid, AccountAuthentication authentication) {
        final Spot spot = spotPersistenceService.findSpot(spotUid, companyUid);
        final SpotMembership spotMembership = getSpotMembership(spot, authentication);
        spotMappingService.updateSpotMembership(spotMembership, spotMembershipManagementRequest);
        log.info("Updating spot membership for spot {} customer {}", spot.getUid(), authentication.getCustomer().getId());
        spotMembershipRepository.save(spotMembership);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, spotMembership.getId());
    }

    public List<SpotMembership> findMembershipsJoinFetchSpots(List<Long> customersIds, Company company) {
        return spotMembershipRepository.findBySpotCompanyAndCustomerIdInJoinFetchSpots(company, customersIds);
    }

    public List<SpotBaseCustomerResponse> findSpots(AccountAuthentication authentication, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Pageable pageable) {
        return spotSearchingService.findSpots(authentication, xCoordinate, yCoordinate, radius, member, namePrefix, pageable);
    }

    @Transactional(readOnly = true)
    public SpotDetailedCustomerResponse findSpot(String spotUid, AccountAuthentication authentication) {
        return spotSearchingService.findSpot(spotUid, authentication);
    }

    public List<SpotBaseCustomerResponse> findSpotsByMemberships(AccountAuthentication authentication) {
        return spotSearchingService.findSpots(authentication);
    }
}
