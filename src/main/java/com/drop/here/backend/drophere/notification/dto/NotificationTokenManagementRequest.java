package com.drop.here.backend.drophere.notification.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTokenManagementRequest {

    @ApiModelProperty(value = "Notification token broadcasting service type", example = "FIREBASE", allowableValues = "FIREBASE", required = true)
    private String broadcastingServiceType;

    @ApiModelProperty(value = "Token", example = "123908y", required = true)
    private String token;
}
