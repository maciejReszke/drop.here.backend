package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropPersistenceService {
    private final DropRepository dropRepository;

    public Drop findDrop(String dropUid, String companyUid) {
        return dropRepository.findByUidAndCompanyUid(dropUid, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Drop with uid %s company %s was not found", dropUid, companyUid),
                        RestExceptionStatusCode.DROP_NOT_FOUND_BY_UID));
    }

}
