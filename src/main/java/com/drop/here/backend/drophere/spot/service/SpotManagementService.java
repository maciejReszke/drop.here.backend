package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.request.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotManagementService {
    private final SpotManagementValidationService spotManagementValidationService;
    private final SpotMappingService spotMappingService;
    private final SpotRepository spotRepository;
    private final SpotMembershipService spotMembershipService;

    public List<SpotCompanyResponse> findCompanySpots(String companyUid, String name) {
        return spotRepository.findAllByCompanyUidAndNameStartsWith(companyUid, StringUtils.defaultIfEmpty(name, ""))
                .stream()
                .map(spotMappingService::toSpotCompanyResponse)
                .collect(Collectors.toList());
    }

    public ResourceOperationResponse createSpot(SpotManagementRequest spotManagementRequest, String companyUid, AccountAuthentication authentication) {
        spotManagementValidationService.validateSpotRequest(spotManagementRequest);
        final Spot spot = spotMappingService.toEntity(spotManagementRequest, authentication);
        log.info("Creating spot for company {} with uid {}", companyUid, spot.getUid());
        spotRepository.save(spot);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, spot.getId());
    }

    public ResourceOperationResponse updateSpot(SpotManagementRequest spotManagementRequest, Long spotId, String companyUid) {
        final Spot spot = getSpot(spotId, companyUid);
        spotManagementValidationService.validateSpotRequest(spotManagementRequest);
        spotMappingService.update(spot, spotManagementRequest);
        log.info("Updating spot for company {} with uid {}", companyUid, spot.getUid());
        spotRepository.save(spot);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, spot.getId());
    }

    private Spot getSpot(Long spotId, String companyUid) {
        return spotRepository.findByIdAndCompanyUid(spotId, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Spot with id %s company %s was not found", spotId, companyUid),
                        RestExceptionStatusCode.SPOT_NOT_FOUND_BY_ID));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResourceOperationResponse deleteSpot(Long spotId, String companyUid) {
        final Spot spot = getSpot(spotId, companyUid);
        log.info("Deleting spot for company {} with uid {}", companyUid, spot.getUid());
        spotMembershipService.deleteMemberships(spot);
        spotRepository.delete(spot);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, spot.getId());
    }

    public Page<SpotCompanyMembershipResponse> findMemberships(Long spotId, String companyUid, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        final Spot spot = getSpot(spotId, companyUid);
        return spotMembershipService.findMemberships(spot, desiredCustomerSubstring, membershipStatus, pageable);
    }

    public ResourceOperationResponse updateMembership(Long spotId, String companyUid, Long membershipId, SpotCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        final Spot spot = getSpot(spotId, companyUid);
        return spotMembershipService.updateMembership(spot, membershipId, companyMembershipManagementRequest);
    }
}
