package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationBroadcastingUtilServiceTest {

    @InjectMocks
    private NotificationBroadcastingUtilService notificationBroadcastingUtilService;

    @Mock
    private NotificationTokenService notificationTokenService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(notificationBroadcastingUtilService, "serverPublicUrl", "localhost:8080", true);
        FieldUtils.writeDeclaredField(notificationBroadcastingUtilService, "faviconEndpoint", "/favicon.ico", true);
        FieldUtils.writeDeclaredField(notificationBroadcastingUtilService, "customerImageEndpoint", "/customers/%s/images", true);
        FieldUtils.writeDeclaredField(notificationBroadcastingUtilService, "companyImageEndpoint", "/companies/%s/images", true);
    }

    @Test
    void givenCompanyNotificationWhenGetNotificationTokenTypeThenGetToken() {
        //given
        final Notification notification = Notification.builder()
                .recipientType(NotificationRecipientType.COMPANY)
                .build();

        when(notificationTokenService.findByType(notification, NotificationTokenType.COMPANY))
                .thenReturn(Optional.of("ziobrotykurwojebana"));

        //when
        final String token = notificationBroadcastingUtilService.getToken(notification);

        //then
        assertThat(token).isEqualTo("ziobrotykurwojebana");
    }

    @Test
    void givenCustomerNotificationWhenGetNotificationTokenTypeThenGetToken() {
        //given
        final Notification notification = Notification.builder()
                .recipientType(NotificationRecipientType.CUSTOMER)
                .build();

        when(notificationTokenService.findByType(notification, NotificationTokenType.CUSTOMER))
                .thenReturn(Optional.of("ziobrotykurwojebana"));

        //when
        final String token = notificationBroadcastingUtilService.getToken(notification);

        //then
        assertThat(token).isEqualTo("ziobrotykurwojebana");
    }

    @Test
    void givenCompanyProfileNotificationWhenGetNotificationTokenTypeThenGetToken() {
        //given
        final Notification notification = Notification.builder()
                .recipientType(NotificationRecipientType.COMPANY_PROFILE)
                .build();

        when(notificationTokenService.findByType(notification, NotificationTokenType.COMPANY_PROFILE))
                .thenReturn(Optional.of("ziobrotykurwojebana"));

        //when
        final String token = notificationBroadcastingUtilService.getToken(notification);

        //then
        assertThat(token).isEqualTo("ziobrotykurwojebana");
    }

    @Test
    void giveEmptyTokenNotificationWhenGetNotificationTokenTypeThenGetToken() {
        //given
        final Notification notification = Notification.builder()
                .recipientType(NotificationRecipientType.COMPANY_PROFILE)
                .build();

        when(notificationTokenService.findByType(notification, NotificationTokenType.COMPANY_PROFILE))
                .thenReturn(Optional.empty());

        //when
        final String token = notificationBroadcastingUtilService.getToken(notification);

        //then
        assertThat(token).isEqualTo("");
    }

    @Test
    void givenBroadcastingCompanyWhenGetImageUrlThenGet() {
        //given
        final Notification notification = Notification.builder()
                .broadcastingCompany(Company.builder().id(5L).build())
                .broadcastingType(NotificationBroadcastingType.COMPANY)
                .build();

        //when
        final String url = notificationBroadcastingUtilService.getImageUrl(notification);

        //then
        assertThat(url).isEqualTo("localhost:8080/companies/5/images");
    }

    @Test
    void givenBroadcastingCustomerWhenGetImageUrlThenGet() {
        //given
        final Notification notification = Notification.builder()
                .broadcastingCustomer(Customer.builder().id(5L).build())
                .broadcastingType(NotificationBroadcastingType.CUSTOMER)
                .build();

        //when
        final String url = notificationBroadcastingUtilService.getImageUrl(notification);

        //then
        assertThat(url).isEqualTo("localhost:8080/customers/5/images");
    }

    @Test
    void givenBroadcastingSystemWhenGetImageUrlThenGet() {
        //given
        final Notification notification = Notification.builder()
                .broadcastingType(NotificationBroadcastingType.SYSTEM)
                .build();

        //when
        final String url = notificationBroadcastingUtilService.getImageUrl(notification);

        //then
        assertThat(url).isEqualTo("localhost:8080/favicon.ico");
    }
}