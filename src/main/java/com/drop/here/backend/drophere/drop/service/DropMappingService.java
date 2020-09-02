package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyResponse;
import com.drop.here.backend.drophere.drop.dto.response.DropMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DropMappingService {

    @Value("${drops.uidGenerator.namePartLength}")
    private int namePartLength;

    @Value("${drops.uidGenerator.randomPartLength}")
    private int randomPartLength;

    public Drop toEntity(DropManagementRequest dropManagementRequest, AccountAuthentication authentication) {
        final Drop drop = Drop.builder()
                .createdAt(LocalDateTime.now())
                .company(authentication.getCompany())
                .build();
        update(drop, dropManagementRequest);
        return drop;
    }

    public void update(Drop drop, DropManagementRequest dropManagementRequest) {
        final DropLocationType locationType = DropLocationType.valueOf(dropManagementRequest.getLocationDropType());
        drop.setUid(generateUid(dropManagementRequest.getName()));
        drop.setYCoordinate(locationType == DropLocationType.GEOLOCATION ? dropManagementRequest.getYCoordinate() : null);
        drop.setXCoordinate(locationType == DropLocationType.GEOLOCATION ? dropManagementRequest.getXCoordinate() : null);
        drop.setEstimatedRadiusMeters(locationType == DropLocationType.GEOLOCATION ? dropManagementRequest.getEstimatedRadiusMeters() : null);
        drop.setRequiresPassword(dropManagementRequest.isRequiresPassword());
        drop.setRequiresAccept(dropManagementRequest.isRequiresAccept());
        drop.setPassword(dropManagementRequest.isRequiresPassword() ? dropManagementRequest.getPassword() : null);
        drop.setLocationType(locationType);
        drop.setHidden(dropManagementRequest.isHidden());
        drop.setDescription(dropManagementRequest.getDescription());
        drop.setLastUpdatedAt(LocalDateTime.now());
        drop.setName(dropManagementRequest.getName().trim());
    }

    private String generateUid(String name) {
        final String startUid = name.length() > namePartLength ? name.substring(0, namePartLength) : name;
        return startUid + RandomStringUtils.randomAlphanumeric(randomPartLength);
    }

    public DropCompanyResponse toDropCompanyResponse(Drop drop) {
        return DropCompanyResponse.builder()
                .id(drop.getId())
                .name(drop.getName())
                .description(drop.getDescription())
                .uid(drop.getUid())
                .hidden(drop.isHidden())
                .requiresPassword(drop.isRequiresPassword())
                .password(drop.getPassword())
                .locationType(drop.getLocationType())
                .requiresAccept(drop.isRequiresAccept())
                .xCoordinate(drop.getXCoordinate())
                .yCoordinate(drop.getYCoordinate())
                .estimatedRadiusMeters(drop.getEstimatedRadiusMeters())
                .createdAt(drop.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .lastUpdatedAt(drop.getLastUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public DropMembership createMembership(Drop drop, DropJoinRequest dropJoinRequest, AccountAuthentication authentication) {
        return DropMembership.builder()
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .customer(authentication.getCustomer())
                .receiveNotification(dropJoinRequest.isReceiveNotification())
                .drop(drop)
                .membershipStatus(drop.isRequiresAccept() ? DropMembershipStatus.PENDING : DropMembershipStatus.ACTIVE)
                .build();
    }

    public DropMembershipResponse toDropMembershipResponse(DropMembership dropMembership) {
        return DropMembershipResponse.builder()
                .dropMembershipStatus(dropMembership.getMembershipStatus())
                .build();
    }
}
