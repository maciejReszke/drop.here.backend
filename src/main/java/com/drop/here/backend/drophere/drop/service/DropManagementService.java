package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DropManagementService {
    private final DropManagementValidationService dropManagementValidationService;
    private final DropMappingService dropMappingService;
    private final DropRepository dropRepository;

    public List<DropCompanyResponse> findCompanyDrops(String companyUid, String name) {
        return dropRepository.findAllByCompanyUidAndNameStartsWith(companyUid, StringUtils.defaultIfEmpty(name, ""))
                .stream()
                .map(dropMappingService::toDropCompanyResponse)
                .collect(Collectors.toList());
    }

    public ResourceOperationResponse createDrop(DropManagementRequest dropManagementRequest, String companyUid, AccountAuthentication authentication) {
        dropManagementValidationService.validateDropRequest(dropManagementRequest);
        final Drop drop = dropMappingService.toEntity(dropManagementRequest, authentication);
        log.info("Creating drop for company {} with uid {}", companyUid, drop.getUid());
        dropRepository.save(drop);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, drop.getId());
    }

    public ResourceOperationResponse updateDrop(DropManagementRequest dropManagementRequest, Long dropId, String companyUid) {
        final Drop drop = getDrop(dropId, companyUid);
        dropManagementValidationService.validateDropRequest(dropManagementRequest);
        dropMappingService.update(drop, dropManagementRequest);
        log.info("Updating drop for company {} with uid {}", companyUid, drop.getUid());
        dropRepository.save(drop);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, drop.getId());
    }

    private Drop getDrop(Long dropId, String companyUid) {
        return dropRepository.findByIdAndCompanyUid(dropId, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with id %s company %s was not found", dropId, companyUid),
                        RestExceptionStatusCode.DROP_NOT_FOUND_BY_ID));
    }

    // TODO: 31/08/2020 usunac wszystkie membershipy!
    public ResourceOperationResponse deleteDrop(Long dropId, String companyUid) {
        final Drop drop = getDrop(dropId, companyUid);
        log.info("Deleting drop for company {} with uid {}", companyUid, drop.getUid());
        dropRepository.delete(drop);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, drop.getId());
    }

    // TODO: 28/08/2020 test
    public Drop findDrop(String dropUid, String companyUid) {
        return dropRepository.findByUidAndCompanyUid(dropUid, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with uid %s company %s was not found", dropUid, companyUid),
                        RestExceptionStatusCode.DROP_NOT_FOUND_BY_UID));
    }
}
