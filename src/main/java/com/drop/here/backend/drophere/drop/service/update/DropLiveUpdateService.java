package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
import org.springframework.stereotype.Service;

@Service
public class DropLiveUpdateService implements DropUpdateService {

    // TODO: 04/10/2020 test, implement
    @Override
    public DropStatus update(Drop drop, Spot spot, Company company, DropManagementRequest dropManagementRequest) {
        return null;
    }
}
