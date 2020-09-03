package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyMembershipResponse;
import com.drop.here.backend.drophere.drop.dto.response.DropMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
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
public class DropMembershipService {
    private final DropPersistenceService dropPersistenceService;
    private final DropMappingService dropMappingService;
    private final DropMembershipRepository dropMembershipRepository;
    private final DropManagementValidationService dropManagementValidationService;
    private final DropMembershipSearchingService dropMembershipSearchingService;

    public Page<DropMembershipResponse> findMemberships(AccountAuthentication authentication, String name, Pageable pageable) {
        return dropMembershipRepository.findByCustomerAndDropNameStartsWithAndMembershipStatusNot(authentication.getCustomer(), name, DropMembershipStatus.BLOCKED, pageable)
                .map(dropMappingService::toDropMembershipResponse);
    }

    public Page<DropCompanyMembershipResponse> findMemberships(Drop drop, String desiredCustomerSubstring, String membershipStatus, Pageable pageable) {
        return dropMembershipSearchingService.findMemberships(drop, desiredCustomerSubstring, membershipStatus, pageable);
    }

    public ResourceOperationResponse createDropMembership(DropJoinRequest dropJoinRequest, String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropPersistenceService.findDrop(dropUid, companyUid);
        dropManagementValidationService.validateJoinDropRequest(drop, dropJoinRequest, authentication.getCustomer());
        final DropMembership membership = dropMappingService.createMembership(drop, dropJoinRequest, authentication);
        log.info("Creating new drop membership for drop {} customer {}", drop.getUid(), authentication.getCustomer().getId());
        dropMembershipRepository.save(membership);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, membership.getId());
    }

    public ResourceOperationResponse deleteDropMembership(String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropPersistenceService.findDrop(dropUid, companyUid);
        final DropMembership dropMembership = getDropMembership(drop, authentication);
        dropManagementValidationService.validateDeleteDropMembership(dropMembership);
        log.info("Deleting drop membership for drop {} customer {}", drop.getUid(), authentication.getCustomer().getId());
        dropMembershipRepository.delete(dropMembership);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, dropMembership.getId());
    }

    private DropMembership getDropMembership(Drop drop, AccountAuthentication authentication) {
        return dropMembershipRepository.findByDropAndCustomer(drop, authentication.getCustomer())
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop membership for customer %s drop %s was not found", authentication.getCustomer().getId(), drop.getId()),
                        RestExceptionStatusCode.DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND
                ));
    }

    private DropMembership getDropMembership(Drop drop, Long membershipId) {
        return dropMembershipRepository.findByIdAndDrop(membershipId, drop)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop membership with id %s drop %s was not found", membershipId, drop.getId()),
                        RestExceptionStatusCode.DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND
                ));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMemberships(Drop drop) {
        dropMembershipRepository.deleteByDrop(drop);
    }

    public ResourceOperationResponse updateMembership(Drop drop, Long membershipId, DropCompanyMembershipManagementRequest companyMembershipManagementRequest) {
        dropManagementValidationService.validateUpdateMembership(companyMembershipManagementRequest);
        final DropMembership dropMembership = getDropMembership(drop, membershipId);
        dropMembership.setMembershipStatus(DropMembershipStatus.valueOf(companyMembershipManagementRequest.getMembershipStatus()));
        dropMembership.setLastUpdatedAt(LocalDateTime.now());
        dropMembershipRepository.save(dropMembership);
        log.info("Updating membership {} status to {}", dropMembership.getId(), companyMembershipManagementRequest.getMembershipStatus());
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, dropMembership.getId());
    }

    public boolean existsMembership(Company company, Long customerId) {
        return dropMembershipRepository.existsByDropCompanyAndCustomerId(company, customerId);
    }

    public ResourceOperationResponse updateDropMembership(DropMembershipManagementRequest dropMembershipManagementRequest, String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropPersistenceService.findDrop(dropUid, companyUid);
        final DropMembership dropMembership = getDropMembership(drop, authentication);
        dropMembership.setReceiveNotification(dropMembershipManagementRequest.isReceiveNotification());
        log.info("Updating drop membership for drop {} customer {}", drop.getUid(), authentication.getCustomer().getId());
        dropMembershipRepository.save(dropMembership);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, dropMembership.getId());
    }

    public List<DropMembership> findMembershipsJoinFetchDrops(List<Long> customersIds, Company company) {
        return dropMembershipRepository.findByDropCompanyAndCustomerIdInJoinFetchDrops(company, customersIds);
    }
}
