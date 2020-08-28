package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
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
        return update(drop, dropManagementRequest);
    }

    public Drop update(Drop drop, DropManagementRequest dropManagementRequest) {
        final DropLocationType locationType = DropLocationType.valueOf(dropManagementRequest.getLocationDropType());

        return drop.toBuilder()
                .uid(generateUid(dropManagementRequest.getName()))
                .yCoordinate(locationType == DropLocationType.GEOLOCATION ? dropManagementRequest.getYCoordinate() : null)
                .xCoordinate(locationType == DropLocationType.GEOLOCATION ? dropManagementRequest.getXCoordinate() : null)
                .estimatedRadiusMeters(locationType == DropLocationType.GEOLOCATION ? dropManagementRequest.getEstimatedRadiusMeters() : null)
                .requiresPassword(dropManagementRequest.isRequiresPassword())
                .requiresAccept(dropManagementRequest.isRequiresAccept())
                .password(dropManagementRequest.isRequiresPassword() ? dropManagementRequest.getPassword() : null)
                .locationType(locationType)
                .hidden(dropManagementRequest.isHidden())
                .description(dropManagementRequest.getDescription())
                .lastUpdatedAt(LocalDateTime.now())
                .name(dropManagementRequest.getName().trim())
                .build();
    }

    private String generateUid(String name) {
        final String startUid = name.length() > namePartLength ? name.substring(0, namePartLength) : name;
        return startUid + RandomStringUtils.randomAlphanumeric(randomPartLength);
    }

    public DropCompanyResponse toDropCompanyResponse(Drop drop) {
        return DropCompanyResponse.builder()
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

    // TODO: 28/08/2020
    public DropMembership createMembership(Drop drop, AccountAuthentication authentication) {

    }
}
