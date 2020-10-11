package com.drop.here.backend.drophere.notification.dto;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NotificationCreationRequest {
    String title;
    String message;
    String detailedMessage;
    NotificationType notificationType;
    NotificationCategory notificationCategory;
    NotificationBroadcastingType broadcastingType;
    NotificationReferencedSubjectType referencedSubjectType;
    String referencedSubjectId;
    Company broadcastingCompany;
    Customer broadcastingCustomer;
    List<Customer> recipientCustomers;
    List<Company> recipientCompanies;
    List<AccountProfile> recipientAccountProfiles;
}
