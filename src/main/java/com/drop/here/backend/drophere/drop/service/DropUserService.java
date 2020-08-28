package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DropUserService {

    // TODO: 28/08/2020 test, implement
    public ResourceOperationResponse createDropMembership(DropJoinRequest dropJoinRequest, String dropUid, String companyUid, AccountAuthentication authentication) {
        return null;
    }

    // TODO: 28/08/2020  test, implement
    public ResourceOperationResponse deleteDrop(String dropUid, String companyUid, AccountAuthentication authentication) {
        return null;
    }

    // TODO: 28/08/2020 test, implement
    public ResourceOperationResponse findMemberships(AccountAuthentication authentication, String name, Pageable pageable) {
        return null;
    }
}
