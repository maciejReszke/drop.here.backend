package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DropUserService {
    private final DropManagementService dropManagementService;
    private final DropManagementValidationService dropManagementValidationService;
    private final DropMappingService dropMappingService;
    private final DropMembershipRepository dropMembershipRepository;

    // TODO: 28/08/2020 test, implement - wciaz brakuje customera
    public ResourceOperationResponse findMemberships(AccountAuthentication authentication, String name, Pageable pageable) {
        return null;
    }

    // TODO: 28/08/2020 test + w logu zamiast get name get customer uid!!
    public ResourceOperationResponse createDropMembership(DropJoinRequest dropJoinRequest, String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropManagementService.findDrop(dropUid, companyUid);
        dropManagementValidationService.validateCreatingDropMembershipRequest(drop, dropJoinRequest);
        final DropMembership membership = dropMappingService.createMembership(drop, authentication);
        log.info("Creating new drop membership for drop {} customer {}", dropUid, authentication.getName());
        dropMembershipRepository.save(membership);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, membership.getId());
    }

    // TODO: 28/08/2020  test + customer? + w logu customeruid
    public ResourceOperationResponse deleteDropMembership(String dropUid, String companyUid, AccountAuthentication authentication) {
        final Drop drop = dropManagementService.findDrop(dropUid, companyUid);
        final DropMembership dropMembership = getDropMembership(drop, authentication);
        log.info("Deleting drop membership for drop {} customer {}", dropUid, authentication.getName());
        dropMembershipRepository.delete(dropMembership);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, dropMembership.getId());
    }

    // TODO: 28/08/2020 powinno szukac jak bedzie mialo customera
    private DropMembership getDropMembership(Drop drop, AccountAuthentication authentication) {
        return null;
    }
}
