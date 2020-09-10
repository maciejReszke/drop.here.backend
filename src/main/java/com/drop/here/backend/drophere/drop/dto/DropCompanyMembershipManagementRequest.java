package com.drop.here.backend.drophere.drop.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropCompanyMembershipManagementRequest {

    @ApiModelProperty(value = "Membership status", example = "ACTIVE", allowableValues = "ACTIVE, BLOCKED", required = true)
    private String membershipStatus;
}
