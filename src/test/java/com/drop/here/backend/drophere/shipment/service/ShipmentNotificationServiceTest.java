package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShipmentNotificationServiceTest {

    @InjectMocks
    private ShipmentNotificationService shipmentNotificationService;

    @Mock
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<NotificationCreationRequest> notificationCreationRequestArgumentCaptor;

    @Test
    void givenShipmentToCustomerNotificationWhenCreateNotificationsThenCreate(){
        //given
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder()
                .name("companyname").build();
        final Shipment shipment = Shipment.builder()
                .customer(customer)
                .id(5L)
                .company(company)
                .build();
        final ShipmentStatus shipmentStatus = ShipmentStatus.ACCEPTED;

        doNothing().when(notificationService).createNotifications(any());

        //when
        shipmentNotificationService.createNotifications(shipment, shipmentStatus, true, false);

        //then
        verify(notificationService).createNotifications(notificationCreationRequestArgumentCaptor.capture());

        final NotificationCreationRequest request = notificationCreationRequestArgumentCaptor.getValue();

        assertThat(request.getBroadcastingCompany()).isEqualTo(company);
        assertThat(request.getDetailedMessage()).isNull();
        assertThat(request.getMessage()).isEqualTo("Your order from company companyname changed status to ACCEPTED!");
        assertThat(request.getTitle()).isEqualTo("Your order is now ACCEPTED!");
        assertThat(request.getBroadcastingCustomer()).isNull();
        assertThat(request.getBroadcastingType()).isEqualTo(NotificationBroadcastingType.COMPANY);
        assertThat(request.getNotificationCategory()).isEqualTo(NotificationCategory.SHIPMENT_STATUS_CHANGE);
        assertThat(request.getNotificationType()).isEqualTo(NotificationType.NOTIFICATION_PANEL);
        assertThat(request.getRecipientAccountProfiles()).isNull();
        assertThat(request.getRecipientCompanies()).isNull();
        assertThat(request.getRecipientCustomers()).hasSize(1);
        assertThat(request.getRecipientCustomers()).contains(customer);
        assertThat(request.getReferencedSubjectType()).isEqualTo(NotificationReferencedSubjectType.SHIPMENT);
        assertThat(request.getReferencedSubjectId()).isEqualTo("5");
    }

    @Test
    void givenShipmentToCompanyWithSellerNotificationWhenCreateNotificationsThenCreate(){
        //given
        final Customer customer = Customer.builder().firstName("Ma").lastName("Ciej").build();
        final Company company = Company.builder()
                .name("companyname").build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final Route route = Route.builder().withSeller(true)
                .profile(accountProfile).build();
        final Drop drop = Drop.builder().route(route).build();
        final Shipment shipment = Shipment.builder()
                .customer(customer)
                .id(5L)
                .company(company)
                .drop(drop)
                .build();
        final ShipmentStatus shipmentStatus = ShipmentStatus.ACCEPTED;

        doNothing().when(notificationService).createNotifications(any());

        //when
        shipmentNotificationService.createNotifications(shipment, shipmentStatus, false, true);

        //then
        verify(notificationService).createNotifications(notificationCreationRequestArgumentCaptor.capture());

        final NotificationCreationRequest request = notificationCreationRequestArgumentCaptor.getValue();

        assertThat(request.getBroadcastingCompany()).isNull();
        assertThat(request.getDetailedMessage()).isNull();
        assertThat(request.getMessage()).isEqualTo("Order from Ma Ciej changed status to ACCEPTED!");
        assertThat(request.getTitle()).isEqualTo("Order has status ACCEPTED!");
        assertThat(request.getBroadcastingCustomer()).isEqualTo(customer);
        assertThat(request.getBroadcastingType()).isEqualTo(NotificationBroadcastingType.CUSTOMER);
        assertThat(request.getNotificationCategory()).isEqualTo(NotificationCategory.SHIPMENT_STATUS_CHANGE);
        assertThat(request.getNotificationType()).isEqualTo(NotificationType.PUSH_NOTIFICATION_ONLY);
        assertThat(request.getRecipientAccountProfiles()).hasSize(1);
        assertThat(request.getRecipientAccountProfiles()).contains(accountProfile);
        assertThat(request.getRecipientCompanies()).hasSize(1);
        assertThat(request.getRecipientCompanies()).contains(company);
        assertThat(request.getRecipientCustomers()).isNull();
        assertThat(request.getReferencedSubjectType()).isEqualTo(NotificationReferencedSubjectType.SHIPMENT);
        assertThat(request.getReferencedSubjectId()).isEqualTo("5");
    }

    @Test
    void givenShipmentToCompanyWithoutSellerNotificationWhenCreateNotificationsThenCreate(){
        //given
        final Customer customer = Customer.builder().firstName("Ma").lastName("Ciej").build();
        final Company company = Company.builder()
                .name("companyname").build();
        final Route route = Route.builder().withSeller(false).build();
        final Drop drop = Drop.builder().route(route).build();
        final Shipment shipment = Shipment.builder()
                .customer(customer)
                .id(null)
                .company(company)
                .drop(drop)
                .build();
        final ShipmentStatus shipmentStatus = ShipmentStatus.ACCEPTED;

        doNothing().when(notificationService).createNotifications(any());

        //when
        shipmentNotificationService.createNotifications(shipment, shipmentStatus, false, true);

        //then
        verify(notificationService).createNotifications(notificationCreationRequestArgumentCaptor.capture());

        final NotificationCreationRequest request = notificationCreationRequestArgumentCaptor.getValue();

        assertThat(request.getBroadcastingCompany()).isNull();
        assertThat(request.getDetailedMessage()).isNull();
        assertThat(request.getMessage()).isEqualTo("Order from Ma Ciej changed status to ACCEPTED!");
        assertThat(request.getTitle()).isEqualTo("Order has status ACCEPTED!");
        assertThat(request.getBroadcastingCustomer()).isEqualTo(customer);
        assertThat(request.getBroadcastingType()).isEqualTo(NotificationBroadcastingType.CUSTOMER);
        assertThat(request.getNotificationCategory()).isEqualTo(NotificationCategory.SHIPMENT_STATUS_CHANGE);
        assertThat(request.getNotificationType()).isEqualTo(NotificationType.PUSH_NOTIFICATION_ONLY);
        assertThat(request.getRecipientAccountProfiles()).isNull();
        assertThat(request.getRecipientCompanies()).hasSize(1);
        assertThat(request.getRecipientCompanies()).contains(company);
        assertThat(request.getRecipientCustomers()).isNull();
        assertThat(request.getReferencedSubjectType()).isEqualTo(NotificationReferencedSubjectType.SHIPMENT);
        assertThat(request.getReferencedSubjectId()).isEmpty();
    }
}