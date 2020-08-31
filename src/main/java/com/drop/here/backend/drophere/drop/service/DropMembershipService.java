package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DropMembershipService {
    private final DropManagementService dropManagementService;
    private final DropMappingService dropMappingService;
    private final DropMembershipRepository dropMembershipRepository;
    private final DropManagementValidationService dropManagementValidationService;

    public Page<DropMembershipResponse> findMemberships(AccountAuthentication authentication, String name, Pageable pageable) {
        return dropMembershipRepository.findByCustomerAndDropNameStartsWith(authentication.getCustomer(), name, pageable)
                .map(dropMappingService::toDropMembershipResponse);
    }

    public ResourceOperationResponse createDropMembership(DropJoinRequest dropJoinRequest, String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropManagementService.findDrop(dropUid, companyUid);
        dropManagementValidationService.validateCreatingDropMembershipRequest(drop, dropJoinRequest);
        final DropMembership membership = dropMappingService.createMembership(drop, authentication);
        log.info("Creating new drop membership for drop {} customer {}", drop.getUid(), authentication.getCustomer().getId());
        dropMembershipRepository.save(membership);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, membership.getId());
    }

    public ResourceOperationResponse deleteDropMembership(String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropManagementService.findDrop(dropUid, companyUid);
        final DropMembership dropMembership = getDropMembership(drop, authentication);
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
}
