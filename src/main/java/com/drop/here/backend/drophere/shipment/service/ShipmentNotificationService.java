package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipmentNotificationService {
    private final NotificationService notificationService;

    public void createNotifications(Shipment shipment, ShipmentStatus status, boolean toCustomer, boolean toCompany) {
        if (toCompany) {
            createToCompanyNotification(shipment, status);
        }

        if (toCustomer) {
            createToCustomerNotification(shipment, status);
        }
    }

    private void createToCustomerNotification(Shipment shipment, ShipmentStatus status) {
        notificationService.createNotifications(prepareToCustomerNotification(shipment, status));
    }

    private NotificationCreationRequest prepareToCustomerNotification(Shipment shipment, ShipmentStatus status) {
        final Company company = shipment.getCompany();
        return NotificationCreationRequest.builder()
                .title(String.format("Your order is now %s!", status))
                .message(String.format("Your order from company %s changed status to %s!", company.getName(), status))
                .notificationType(NotificationType.NOTIFICATION_PANEL)
                .notificationCategory(NotificationCategory.SHIPMENT_STATUS_CHANGE)
                .broadcastingType(NotificationBroadcastingType.COMPANY)
                .broadcastingCompany(company)
                .referencedSubjectType(NotificationReferencedSubjectType.SHIPMENT)
                .referencedSubjectId(Optional.ofNullable(shipment.getId()).map(Object::toString).orElse(""))
                .recipientCustomers(List.of(shipment.getCustomer()))
                .build();
    }

    private void createToCompanyNotification(Shipment shipment, ShipmentStatus status) {
        notificationService.createNotifications(prepareToCompanyNotification(shipment, status));
    }

    private NotificationCreationRequest prepareToCompanyNotification(Shipment shipment, ShipmentStatus status) {
        final Customer customer = shipment.getCustomer();
        final Route route = shipment.getDrop().getRoute();
        return NotificationCreationRequest.builder()
                .title(String.format("Order has status %s!", status))
                .message(String.format("Order from %s %s changed status to %s!", customer.getFirstName(), customer.getLastName(), status))
                .notificationType(NotificationType.PUSH_NOTIFICATION_ONLY)
                .notificationCategory(NotificationCategory.SHIPMENT_STATUS_CHANGE)
                .broadcastingType(NotificationBroadcastingType.CUSTOMER)
                .broadcastingCustomer(customer)
                .recipientCompanies(List.of(shipment.getCompany()))
                .referencedSubjectType(NotificationReferencedSubjectType.SHIPMENT)
                .referencedSubjectId(Optional.ofNullable(shipment.getId()).map(Object::toString).orElse("null"))
                .recipientAccountProfiles(route.isWithSeller() ? List.of(route.getProfile()) : null)
                .build();
    }
}
