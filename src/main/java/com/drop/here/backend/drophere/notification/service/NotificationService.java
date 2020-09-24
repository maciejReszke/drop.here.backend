package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.repository.NotificationJobRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationJobRepository notificationJobRepository;
    private final NotificationMappingService notificationMappingService;
    private final NotificationValidationService notificationValidationService;
    private final NotificationBroadcastingServiceFactory notificationBroadcastingServiceFactory;

    public Flux<NotificationResponse> findNotifications(AccountAuthentication accountAuthentication, String readStatus, Pageable pageable) {
        final List<NotificationReadStatus> desiredReadStatuses = getDesiredReadStatuses(readStatus);
        return findNotifications(accountAuthentication, pageable, desiredReadStatuses)
                .map(notificationMappingService::toNotificationResponse);
    }

    private Flux<Notification> findNotifications(AccountAuthentication accountAuthentication, Pageable pageable, List<NotificationReadStatus> desiredReadStatuses) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY
                ? findCompanyNotifications(accountAuthentication, pageable, desiredReadStatuses)
                : findCustomerNotifications(accountAuthentication, pageable, desiredReadStatuses);
    }

    private Flux<Notification> findCustomerNotifications(AccountAuthentication accountAuthentication, Pageable pageable, List<NotificationReadStatus> desiredReadStatuses) {
        return notificationRepository.findByRecipientCustomerAndReadStatusIn(accountAuthentication.getCustomer(), desiredReadStatuses, pageable);
    }

    private Flux<Notification> findCompanyNotifications(AccountAuthentication accountAuthentication, Pageable pageable, List<NotificationReadStatus> desiredReadStatuses) {
        return notificationRepository.findByRecipientCompanyOrRecipientAccountProfileAndReadStatusIn(accountAuthentication.getCompany(), accountAuthentication.getProfile(), desiredReadStatuses, pageable);
    }

    private List<NotificationReadStatus> getDesiredReadStatuses(String readStatus) {
        return StringUtils.isBlank(readStatus)
                ? Arrays.asList(NotificationReadStatus.values())
                : List.of(NotificationReadStatus.valueOf(readStatus));
    }

    public Mono<ResourceOperationResponse> updateNotification(AccountAuthentication accountAuthentication, String notificationId, NotificationManagementRequest notificationManagementRequest) {
        return findNotification(accountAuthentication, notificationId)
                .doOnNext(notification -> notificationValidationService.validateUpdateNotificationRequest(notificationManagementRequest))
                .doOnNext(notification -> notificationMappingService.update(notification, notificationManagementRequest))
                .flatMap(notificationRepository::save)
                .map(notification -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, notificationId));
    }

    private Mono<Notification> findNotification(AccountAuthentication authentication, String notificationId) {
        final Account principal = authentication.getPrincipal();
        final Mono<Notification> notification = principal.getAccountType() == AccountType.COMPANY
                ? notificationRepository.findByIdAndRecipientCompanyOrRecipientAccountProfile(notificationId, authentication.getCompany(), authentication.getProfile())
                : notificationRepository.findByIdAndRecipientCustomer(notificationId, authentication.getCustomer());
        return notification.switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                "Notification with id %s for account %s was not found", notificationId, principal.getId()),
                RestExceptionStatusCode.NOTIFICATION_BY_ID_FOR_PRINCIPAL_NOT_FOUND)));
    }

    public Mono<Void> sendNotifications() {
        final NotificationBroadcastingService notificationBroadcastingService = notificationBroadcastingServiceFactory.getNotificationBroadcastingService();
        final PageRequest pageable = PageRequest.of(0, notificationBroadcastingService.getBatchAmount());
        return notificationJobRepository.findAllByNotificationIsNotNull(pageable)
                .collectList()
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(notificationJobs -> sendNotifications(notificationJobs, notificationBroadcastingService));
    }

    private Mono<Void> sendNotifications(List<NotificationJob> notifications, NotificationBroadcastingService notificationBroadcastingService) {
        log.info("Sending batch of notifications {}", notifications.size());
        return notificationBroadcastingService.sendBatch(notifications)
                .flatMap(result -> handleSendingNotificationsResult(result, notifications));
    }

    private Mono<Void> handleSendingNotificationsResult(boolean result, List<NotificationJob> notifications) {
        return result
                ? handleSuccessSendingNotificationsResult(notifications)
                : handleFailureSendingNotificationsResult(notifications);
    }

    private Mono<Void> handleSuccessSendingNotificationsResult(List<NotificationJob> notifications) {
        log.info("Successfully send batch {} of notifications", notifications.size());
        return notificationJobRepository.deleteByNotificationJobIn(notifications);
    }

    private Mono<Void> handleFailureSendingNotificationsResult(List<NotificationJob> notifications) {
        log.info("Failed to send batch {} of notifications", notifications.size());
        return Mono.empty();
    }
}
