package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class NotificationBroadcastingUtilService {
    private final NotificationTokenService notificationTokenService;

    @Value("${server.config.publicUrl}")
    private String serverPublicUrl;

    @Value("${server.config.faviconEndpoint}")
    private String faviconEndpoint;

    @Value("${server.config.getCustomerImageEndpoint}")
    private String customerImageEndpoint;

    @Value("${server.config.getCompanyImageEndpoint}")
    private String companyImageEndpoint;

    public String getImageUrl(Notification notification) {
        return API.Match(notification.getBroadcastingType()).of(
                Case($(NotificationBroadcastingType.COMPANY), () -> getBroadcastingCompanyImageUrl(notification)),
                Case($(NotificationBroadcastingType.SYSTEM), this::getBroadcastingSystemImageUrl),
                Case($(NotificationBroadcastingType.CUSTOMER), () -> getBroadcastingCustomerImageUrl(notification))
        );
    }

    private String getBroadcastingCustomerImageUrl(Notification notification) {
        return serverPublicUrl + String.format(customerImageEndpoint, notification.getBroadcastingCustomer().getId());
    }

    private String getBroadcastingSystemImageUrl() {
        return serverPublicUrl + faviconEndpoint;
    }

    private String getBroadcastingCompanyImageUrl(Notification notification) {
        return serverPublicUrl + String.format(companyImageEndpoint, notification.getBroadcastingCompany().getId());
    }

    public String getToken(Notification notification) {
        final NotificationTokenType notificationTokenType = getNotificationTokenType(notification);
        return notificationTokenService.findByType(notification, notificationTokenType).orElse("");
    }

    private NotificationTokenType getNotificationTokenType(Notification notification) {
        return API.Match(notification.getRecipientType()).of(
                Case($(NotificationRecipientType.COMPANY), NotificationTokenType.COMPANY),
                Case($(NotificationRecipientType.CUSTOMER), NotificationTokenType.CUSTOMER),
                Case($(NotificationRecipientType.COMPANY_PROFILE), NotificationTokenType.COMPANY_PROFILE)
        );
    }
}
