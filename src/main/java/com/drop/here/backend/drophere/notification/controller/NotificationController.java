package com.drop.here.backend.drophere.notification.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Api(tags = "Notifications API")

public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationTokenService notificationTokenService;

    @GetMapping
    @ApiOperation("Get notifications")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notifications for principal"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Flux<NotificationResponse> findNotifications(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                        @ApiParam(value = "Notification read type", allowableValues = "READ, UNREAD, null") @RequestParam(required = false) String readStatus,
                                                        Pageable pageable) {
        return accountAuthenticationMono.flatMapMany(accountAuthentication -> notificationService.findNotifications(accountAuthentication, readStatus, pageable));
    }

    @PutMapping("/{notificationId}")
    @ApiOperation("Update notification read status")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notification updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateNotification(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                              @RequestBody @Valid Mono<NotificationManagementRequest> notificationManagementRequestMono,
                                                              @PathVariable Long notificationId) {
        return accountAuthenticationMono.zipWith(notificationManagementRequestMono)
                .flatMap(tuple -> notificationService.updateNotification(tuple.getT1(), notificationId, tuple.getT2()));
    }

    @PutMapping("/tokens")
    @ApiOperation("Update notification token")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Token updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateNotificationToken(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                                   @RequestBody @Valid Mono<NotificationTokenManagementRequest> notificationTokenManagementRequestMono) {
        return accountAuthenticationMono.zipWith(notificationTokenManagementRequestMono)
                .flatMap(tuple -> notificationTokenService.updateNotificationToken(tuple.getT1(), tuple.getT2()));
    }
}
