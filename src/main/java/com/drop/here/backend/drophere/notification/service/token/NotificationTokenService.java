package com.drop.here.backend.drophere.notification.service.token;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;

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

    public ResourceOperationResponse updateNotificationToken(AccountAuthentication accountAuthentication, NotificationTokenManagementRequest notificationTokenManagementRequest) {
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

    // TODO: 06/10/2020 test
    public List<NotificationAndTokenWrapper> joinTokens(List<Notification> notifications, NotificationBroadcastingServiceType type) {
        return notifications.stream()
                .collect(Collectors.groupingBy(Notification::getRecipientType))
                .entrySet()
                .stream()
                .map(tuple -> joinTokens(tuple.getKey(), tuple.getValue(), type))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private List<NotificationAndTokenWrapper> joinTokens(NotificationRecipientType recipientType, List<Notification> notifications, NotificationBroadcastingServiceType type) {
        return API.Match(recipientType).of(
                Case($(NotificationRecipientType.COMPANY_PROFILE), () -> joinCompanyProfileTokens(notifications, type)),
                Case($(NotificationRecipientType.COMPANY), () -> joinCompanyTokens(notifications)),
                Case($(NotificationRecipientType.CUSTOMER), () -> joinCustomerTokens(notifications, type))
        );
    }

    private List<NotificationAndTokenWrapper> joinCustomerTokens(List<Notification> notifications, NotificationBroadcastingServiceType broadcastingServiceType) {
        final List<Customer> customers = notifications.stream()
                .map(Notification::getRecipientCustomer)
                .collect(Collectors.toList());

        final List<NotificationToken> tokens = notificationTokenRepository
                .findByOwnerCustomerInAndBroadcastingServiceType(customers, broadcastingServiceType);
        return notifications.stream()
                .map(notification -> new NotificationAndTokenWrapper(notification, getTokenForCustomer(notification.getRecipientCustomer(), tokens)))
                .collect(Collectors.toList());
    }

    private Optional<NotificationToken> getTokenForCustomer(Customer customer, List<NotificationToken> tokens) {
        return tokens.stream()
                .filter(token -> token.getOwnerCustomer().getId().equals(customer.getId()))
                .findFirst();
    }

    private List<NotificationAndTokenWrapper> joinCompanyTokens(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> new NotificationAndTokenWrapper(notification, Optional.empty()))
                .collect(Collectors.toList());
    }

    private List<NotificationAndTokenWrapper> joinCompanyProfileTokens(List<Notification> notifications, NotificationBroadcastingServiceType serviceType) {
        final List<AccountProfile> profiles = notifications.stream()
                .map(Notification::getRecipientAccountProfile)
                .collect(Collectors.toList());

        final List<NotificationToken> tokens = notificationTokenRepository
                .findByOwnerAccountProfileInAndBroadcastingServiceType(profiles, serviceType);
        return notifications.stream()
                .map(notification -> new NotificationAndTokenWrapper(notification, getTokenForAccountProfile(notification.getRecipientAccountProfile(), tokens)))
                .collect(Collectors.toList());
    }

    private Optional<NotificationToken> getTokenForAccountProfile(AccountProfile accountProfile, List<NotificationToken> tokens) {
        return tokens.stream()
                .filter(token -> token.getOwnerAccountProfile().getId().equals(accountProfile.getId()))
                .findFirst();
    }
}
