package com.drop.here.backend.drophere.drop.dto;

import lombok.Builder;
import lombok.Data;

// TODO: 04/10/2020
@Builder
@Data
public class DropManagementRequest {
    DropStatusChange newStatus;
}
