package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;

public interface DropUpdateService {
    DropStatus update(Drop drop, Spot spot, Company company, DropManagementRequest dropManagementRequest);
}
