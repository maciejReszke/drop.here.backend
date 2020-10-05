package com.drop.here.backend.drophere.notification.entity;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationReadStatus readStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationReferencedSubjectType referencedSubjectType;

    @NotBlank
    private String referencedSubjectId;

    private String detailedMessage;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationBroadcastingType broadcastingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broadcasting_company_id")
    private Company broadcastingCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broadcasting_customer_id")
    private Customer broadcastingCustomer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationRecipientType recipientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_customer_id")
    private Customer recipientCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_company_id")
    private Company recipientCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_account_profile_id")
    private AccountProfile recipientAccountProfile;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @Version
    private Long version;
}
