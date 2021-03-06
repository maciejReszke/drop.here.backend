package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.service.UidGeneratorService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SpotMappingService {
    private final UidGeneratorService uidGeneratorService;

    @Value("${spots.uid_generator.name_part_length}")
    private int namePartLength;

    @Value("${spots.uid_generator.random_part_length}")
    private int randomPartLength;

    public Spot toEntity(SpotManagementRequest spotManagementRequest, AccountAuthentication authentication) {
        final Spot spot = Spot.builder()
                .createdAt(LocalDateTime.now())
                .company(authentication.getCompany())
                .build();
        update(spot, spotManagementRequest);
        return spot;
    }

    public void update(Spot spot, SpotManagementRequest spotManagementRequest) {
        boolean nameChanged = !spotManagementRequest.getName().equals(spot.getName());
        spot.setUid(nameChanged ? generateUid(spotManagementRequest.getName()) : spot.getUid());
        spot.setYCoordinate(spotManagementRequest.getYCoordinate());
        spot.setXCoordinate(spotManagementRequest.getXCoordinate());
        spot.setEstimatedRadiusMeters(spotManagementRequest.getEstimatedRadiusMeters());
        spot.setRequiresPassword(spotManagementRequest.isRequiresPassword());
        spot.setRequiresAccept(spotManagementRequest.isRequiresAccept());
        spot.setPassword(spotManagementRequest.isRequiresPassword() ? spotManagementRequest.getPassword() : null);
        spot.setHidden(spotManagementRequest.isHidden());
        spot.setDescription(spotManagementRequest.getDescription());
        spot.setLastUpdatedAt(LocalDateTime.now());
        spot.setName(spotManagementRequest.getName().trim());
    }

    private String generateUid(String name) {
        return uidGeneratorService.generateUid(name, namePartLength, randomPartLength);
    }

    public SpotCompanyResponse toSpotCompanyResponse(Spot spot) {
        return SpotCompanyResponse.builder()
                .id(spot.getId())
                .name(spot.getName())
                .description(spot.getDescription())
                .uid(spot.getUid())
                .hidden(spot.isHidden())
                .requiresPassword(spot.isRequiresPassword())
                .password(spot.getPassword())
                .requiresAccept(spot.isRequiresAccept())
                .xCoordinate(spot.getXCoordinate())
                .yCoordinate(spot.getYCoordinate())
                .estimatedRadiusMeters(spot.getEstimatedRadiusMeters())
                .createdAt(spot.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .lastUpdatedAt(spot.getLastUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public SpotMembership createMembership(Spot spot, SpotJoinRequest spotJoinRequest, AccountAuthentication authentication) {
        return SpotMembership.builder()
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .customer(authentication.getCustomer())
                .spot(spot)
                .membershipStatus(spot.isRequiresAccept() ? SpotMembershipStatus.PENDING : SpotMembershipStatus.ACTIVE)
                .receiveCancelledNotifications(spotJoinRequest.isReceiveCancelledNotifications())
                .receiveDelayedNotifications(spotJoinRequest.isReceiveDelayedNotifications())
                .receiveFinishedNotifications(spotJoinRequest.isReceiveFinishedNotifications())
                .receiveLiveNotifications(spotJoinRequest.isReceiveLiveNotifications())
                .receivePreparedNotifications(spotJoinRequest.isReceivePreparedNotifications())
                .build();
    }

    public void updateSpotMembership(SpotMembership spotMembership, SpotMembershipManagementRequest spotMembershipManagementRequest) {
        spotMembership.setReceiveFinishedNotifications(spotMembershipManagementRequest.isReceiveFinishedNotifications());
        spotMembership.setReceiveCancelledNotifications(spotMembershipManagementRequest.isReceiveCancelledNotifications());
        spotMembership.setReceivePreparedNotifications(spotMembershipManagementRequest.isReceivePreparedNotifications());
        spotMembership.setReceiveLiveNotifications(spotMembershipManagementRequest.isReceiveLiveNotifications());
        spotMembership.setReceiveDelayedNotifications(spotMembershipManagementRequest.isReceiveDelayedNotifications());
    }
}
