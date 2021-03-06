package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.notification.repository.NotificationJobRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingServiceFactory;
import com.drop.here.backend.drophere.notification.service.token.NotificationAndTokenWrapper;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationJobRepository notificationJobRepository;
    private final NotificationMappingService notificationMappingService;
    private final NotificationValidationService notificationValidationService;
    private final NotificationBroadcastingServiceFactory notificationBroadcastingServiceFactory;
    private final NotificationTokenService notificationTokenService;

    @Transactional
    public void createNotifications(NotificationCreationRequest request) {
        final List<Notification> preFilteredNotifications = notificationMappingService.toNotifications(request);
        final List<NotificationAndTokenWrapper> joinedTokens = joinTokens(preFilteredNotifications);
        final List<NotificationAndTokenWrapper> notificationWrappersToBePersisted = filterValidNotifications(joinedTokens);
        final List<NotificationJob> jobs = createJobs(notificationWrappersToBePersisted);
        final List<Notification> notifications = unwrapNotifications(notificationWrappersToBePersisted);
        notificationRepository.saveAll(notifications);
        notificationJobRepository.saveAll(jobs);
    }

    private List<Notification> unwrapNotifications(List<NotificationAndTokenWrapper> notificationWrappersToBePersisted) {
        return notificationWrappersToBePersisted.stream()
                .map(NotificationAndTokenWrapper::getNotification)
                .collect(Collectors.toList());
    }

    private List<NotificationJob> createJobs(List<NotificationAndTokenWrapper> notificationWrappersToBePersisted) {
        return notificationWrappersToBePersisted.stream()
                .filter(this::hasToken)
                .map(wrapper -> notificationMappingService.toNotificationJob(wrapper.getNotification(), wrapper.getToken().orElseThrow()))
                .collect(Collectors.toList());
    }

    private List<NotificationAndTokenWrapper> filterValidNotifications(List<NotificationAndTokenWrapper> joinedTokens) {
        return joinedTokens
                .stream()
                .filter(wrapper -> isPushOnlyNotification(wrapper) || hasToken(wrapper))
                .collect(Collectors.toList());
    }

    private List<NotificationAndTokenWrapper> joinTokens(List<Notification> preFilteredNotifications) {
        return notificationTokenService.joinTokens(preFilteredNotifications, NotificationBroadcastingServiceType.FIREBASE);
    }

    private boolean isPushOnlyNotification(NotificationAndTokenWrapper wrapper) {
        return wrapper.getNotification().getType() != NotificationType.PUSH_NOTIFICATION_ONLY;
    }

    private boolean hasToken(NotificationAndTokenWrapper wrapper) {
        return wrapper.getToken().isPresent();
    }

    public Page<NotificationResponse> findNotifications(AccountAuthentication accountAuthentication, String readStatus, Pageable pageable) {
        final List<NotificationReadStatus> desiredReadStatuses = getDesiredReadStatuses(readStatus);
        final Page<Notification> notifications = findNotifications(accountAuthentication, pageable, desiredReadStatuses);
        return notifications.map(notificationMappingService::toNotificationResponse);
    }

    private Page<Notification> findNotifications(AccountAuthentication accountAuthentication, Pageable pageable, List<NotificationReadStatus> desiredReadStatuses) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY
                ? findCompanyNotifications(accountAuthentication, pageable, desiredReadStatuses)
                : findCustomerNotifications(accountAuthentication, pageable, desiredReadStatuses);
    }

    private Page<Notification> findCustomerNotifications(AccountAuthentication accountAuthentication, Pageable pageable, List<NotificationReadStatus> desiredReadStatuses) {
        return notificationRepository.findByRecipientCustomerAndReadStatusInAndType(
                accountAuthentication.getCustomer(), desiredReadStatuses, NotificationType.NOTIFICATION_PANEL, pageable);
    }

    private Page<Notification> findCompanyNotifications(AccountAuthentication accountAuthentication, Pageable pageable, List<NotificationReadStatus> desiredReadStatuses) {
        return notificationRepository.findByRecipientCompanyOrRecipientAccountProfileAndReadStatusIn(accountAuthentication.getCompany(), accountAuthentication.getProfile(), desiredReadStatuses, pageable);
    }

    private List<NotificationReadStatus> getDesiredReadStatuses(String readStatus) {
        return StringUtils.isBlank(readStatus)
                ? Arrays.asList(NotificationReadStatus.values())
                : List.of(NotificationReadStatus.valueOf(readStatus));
    }

    public ResourceOperationResponse updateNotification(AccountAuthentication accountAuthentication, Long notificationId, NotificationManagementRequest notificationManagementRequest) {
        final Notification notification = findNotification(accountAuthentication, notificationId);
        notificationValidationService.validateUpdateNotificationRequest(notificationManagementRequest);
        notificationMappingService.update(notification, notificationManagementRequest);
        log.info("Updating notification with id {}", notificationId);
        notificationRepository.save(notification);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, notificationId);
    }

    private Notification findNotification(AccountAuthentication authentication, Long notificationId) {
        final Account principal = authentication.getPrincipal();
        final Optional<Notification> notification = principal.getAccountType() == AccountType.COMPANY
                ? notificationRepository.findByIdAndRecipientCompanyOrRecipientAccountProfileAndType(
                notificationId, authentication.getCompany(), authentication.getProfile(), NotificationType.NOTIFICATION_PANEL)
                : notificationRepository.findByIdAndRecipientCustomerAndType(
                notificationId, authentication.getCustomer(), NotificationType.NOTIFICATION_PANEL);
        return notification.orElseThrow(() -> new RestEntityNotFoundException(String.format(
                "Notification with id %s for account %s was not found", notificationId, principal.getId()),
                RestExceptionStatusCode.NOTIFICATION_BY_ID_FOR_PRINCIPAL_NOT_FOUND));
    }

    @Transactional
    public void sendNotifications() {
        final NotificationBroadcastingService notificationBroadcastingService = notificationBroadcastingServiceFactory.getNotificationBroadcastingService();
        final PageRequest pageable = PageRequest.of(0, notificationBroadcastingService.getBatchAmount());
        final List<NotificationJob> notifications = notificationJobRepository.findAllByNotificationIsNotNull(pageable);
        if (!notifications.isEmpty()) {
            log.info("Sending batch of notifications {}", notifications.size());
            final boolean result = notificationBroadcastingService.sendBatch(notifications);
            if (result) {
                log.info("Successfully send batch {} of notifications", notifications.size());
                notificationJobRepository.deleteByNotificationJobIn(notifications);
                notificationRepository.deletePushOnlyNotifications(notifications.stream()
                        .map(NotificationJob::getNotification)
                        .collect(Collectors.toList()));
            } else {
                log.info("Failed to send batch {} of notifications", notifications.size());
            }
        }
    }
}
