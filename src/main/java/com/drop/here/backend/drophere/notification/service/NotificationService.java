package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingServiceFactory;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMappingService notificationMappingService;
    private final NotificationValidationService notificationValidationService;
    private final NotificationBroadcastingServiceFactory notificationBroadcastingServiceFactory;

    public Page<NotificationResponse> findNotifications(AccountAuthentication accountAuthentication, String readStatus, Pageable pageable) {
        final List<NotificationReadStatus> desiredReadStatuses = getDesiredReadStatuses(readStatus);
        final Page<Notification> notifications = accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY
                ? findNotifications(accountAuthentication.getCompany(), desiredReadStatuses, pageable)
                : findNotifications(accountAuthentication.getCustomer(), desiredReadStatuses, pageable);
        return notifications.map(notificationMappingService::toNotificationResponse);
    }

    private Page<Notification> findNotifications(Company company, List<NotificationReadStatus> desiredReadStatuses, Pageable pageable) {
        return notificationRepository.findByRecipientCompanyAndReadStatusIn(company, desiredReadStatuses, pageable);
    }

    private Page<Notification> findNotifications(Customer customer, List<NotificationReadStatus> desiredReadStatuses, Pageable pageable) {
        return notificationRepository.findByRecipientCustomerAndReadStatusIn(customer, desiredReadStatuses, pageable);
    }

    private List<NotificationReadStatus> getDesiredReadStatuses(String readStatus) {
        return StringUtils.isBlank(readStatus)
                ? Arrays.asList(NotificationReadStatus.values())
                : List.of(NotificationReadStatus.valueOf(readStatus));
    }

    public ResourceOperationResponse updateNotification(AccountAuthentication accountAuthentication, Long notificationId, NotificationManagementRequest notificationManagementRequest) {
        final Notification notification = findNotification(accountAuthentication.getPrincipal(), notificationId);
        notificationValidationService.validateUpdateNotificationRequest(notificationManagementRequest);
        notificationMappingService.update(notification, notificationManagementRequest);
        log.info("Updating notification with id {}", notificationId);
        notificationRepository.save(notification);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, notificationId);
    }

    private Notification findNotification(Account principal, Long notificationId) {
        final Optional<Notification> notification = principal.getAccountType() == AccountType.COMPANY
                ? notificationRepository.findByIdAndRecipientCompany(notificationId, principal.getCompany())
                : notificationRepository.findByIdAndRecipientCustomer(notificationId, principal.getCustomer());
        return notification.orElseThrow(() -> new RestEntityNotFoundException(String.format(
                "Notification with id %s for account %s was not found", notificationId, principal.getId()),
                RestExceptionStatusCode.NOTIFICATION_BY_ID_FOR_PRINCIPAL_NOT_FOUND));
    }

    public void sendNotifications() {
        final NotificationBroadcastingService notificationBroadcastingService = notificationBroadcastingServiceFactory.getNotificationBroadcastingService();
        final PageRequest pageable = PageRequest.of(0, notificationBroadcastingService.getBatchAmount());
        final List<Notification> notifications = notificationRepository.findByBroadcastingStatus(NotificationBroadcastingStatus.NOT_SENT, pageable);
        log.info("Sending batch of notifications {}", notifications.size());
        final boolean result = notificationBroadcastingService.sendBatch(notifications);
        if (result) {
            log.info("Successfully send batch {} of notifications", notifications.size());
            notificationRepository.updateBroadcastingStatus(notifications, NotificationBroadcastingStatus.SENT);
        } else {
            log.info("Failed to send batch {} of notifications", notifications.size());
        }
    }
}
