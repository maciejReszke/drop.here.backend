package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class NotificationBroadcastingUtilService {

    @Value("${server.config.public_url}")
    private String serverPublicUrl;

    @Value("${server.config.favicon_endpoint}")
    private String faviconEndpoint;

    @Value("${server.config.get_customer_image_endpoint}")
    private String customerImageEndpoint;

    @Value("${server.config.get_company_image_endpoint}")
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
}
