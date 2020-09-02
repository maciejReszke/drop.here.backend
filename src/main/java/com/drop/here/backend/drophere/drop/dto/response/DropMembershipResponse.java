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

    @ApiModelProperty(value = "Does user want to receive notifications", example = "true")
    boolean receiveNotification;

    // TODO: 31/08/2020 Tu też powinny byc informacje o firmie(zrobic razem z filtrowaniem i listowaniem firm) + dopisac do integracyjnego
}
