package com.drop.here.backend.drophere.notification.service.token;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;

// TODO MONO:
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTokenService {
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTokenMappingService notificationTokenMappingService;

    private Optional<NotificationToken> findNotification(NotificationBroadcastingServiceType notificationBroadcastingServiceType, AccountProfile accountProfile, Customer customer, NotificationTokenType tokenType) {
        return API.Match(tokenType).of(
                Case($(NotificationTokenType.PROFILE), () -> notificationTokenRepository.findByOwnerAccountProfileAndBroadcastingServiceType(accountProfile, notificationBroadcastingServiceType)),
                Case($(NotificationTokenType.CUSTOMER), () -> notificationTokenRepository.findByOwnerCustomerAndBroadcastingServiceType(customer, notificationBroadcastingServiceType))
        );
    }

    public Mono<ResourceOperationResponse> updateNotificationToken(AccountAuthentication accountAuthentication, NotificationTokenManagementRequest notificationTokenManagementRequest) {
        final NotificationToken notificationToken = notificationTokenMappingService.toNotificationToken(accountAuthentication, notificationTokenManagementRequest);
        final NotificationToken toBeSavedNotificationToken = findNotification(notificationToken.getBroadcastingServiceType(), notificationToken.getOwnerAccountProfile(), notificationToken.getOwnerCustomer(), notificationToken.getTokenType())
                .orElse(notificationToken);
        toBeSavedNotificationToken.setToken(notificationToken.getToken());

        log.info("Updating token with id {} type {} with principal {} broadcasting service {}",
                toBeSavedNotificationToken.getId(),
                toBeSavedNotificationToken.getTokenType(),
                toBeSavedNotificationToken.getTokenType() == NotificationTokenType.CUSTOMER ? toBeSavedNotificationToken.getOwnerCustomer().getId() : toBeSavedNotificationToken.getOwnerAccountProfile().getId(),
                toBeSavedNotificationToken.getBroadcastingServiceType());

        notificationTokenRepository.save(toBeSavedNotificationToken);

        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, toBeSavedNotificationToken.getId());
    }
}
