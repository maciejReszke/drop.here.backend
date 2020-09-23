package com.drop.here.backend.drophere.notification.entity;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document
public class Notification {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationReadStatus readStatus;

    @NotNull
    private NotificationReferencedSubjectType referencedSubjectType;

    private String detailedMessage;

    @NotNull
    private NotificationBroadcastingType broadcastingType;

    // TODO: 23/09/2020 zobaczyc jak to dziala na bazie
    @DBRef
    private Company broadcastingCompany;

    @DBRef
    private Customer broadcastingCustomer;

    @DBRef
    private Customer recipientCustomer;

    @DBRef
    private Company recipientCompany;

    @DBRef
    private AccountProfile recipientAccountProfile;

    @NotNull
    private NotificationRecipientType recipientType;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @Version
    private Long version;
}
