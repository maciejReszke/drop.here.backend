package com.drop.here.backend.drophere.drop.service.update;

import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;

public interface DropUpdateService {
    Drop update(Drop drop, DropManagementRequest dropManagementRequest);
}
