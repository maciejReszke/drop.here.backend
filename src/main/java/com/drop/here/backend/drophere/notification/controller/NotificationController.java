package com.drop.here.backend.drophere.notification.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // TODO: 06/09/2020  test
    @GetMapping
    @ApiOperation("Get notifications")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Notifications for principal"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Page<NotificationResponse> findNotifications(@ApiIgnore AccountAuthentication accountAuthentication,
                                                        @ApiParam(value = "Notification read type", allowableValues = "READ, UNREAD, null") @RequestParam(required = false) String readStatus,
                                                        Pageable pageable) {
        return notificationService.findNotifications(accountAuthentication, readStatus, pageable);
    }

    @PutMapping("/{notificationId}")
    @ApiOperation("Update notification read status")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Notification updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateNotification(@ApiIgnore AccountAuthentication accountAuthentication,
                                                        @RequestBody @Valid NotificationManagementRequest notificationManagementRequest,
                                                        @PathVariable Long notificationId) {
        return notificationService.updateNotification(accountAuthentication, notificationId, notificationManagementRequest);
    }
}
