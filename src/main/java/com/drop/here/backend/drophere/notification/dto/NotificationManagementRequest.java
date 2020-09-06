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
public class NotificationManagementRequest {

    @ApiModelProperty(value = "Notification read status", example = "READ", allowableValues = "READ, UNREAD", required = true)
    private String readStatus;
}
