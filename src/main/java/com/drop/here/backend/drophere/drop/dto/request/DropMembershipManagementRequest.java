package com.drop.here.backend.drophere.drop.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropMembershipManagementRequest {

    @NotNull
    @ApiModelProperty(value = "Does user want to receive notifications", example = "true")
    private boolean receiveNotification;
}
