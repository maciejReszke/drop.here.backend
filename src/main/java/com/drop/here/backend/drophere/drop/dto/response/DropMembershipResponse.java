package com.drop.here.backend.drophere.drop.dto.response;

import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DropMembershipResponse {

    @ApiModelProperty(value = "Drop membership status", example = "ACTIVE")
    DropMembershipStatus dropMembershipStatus;
}
