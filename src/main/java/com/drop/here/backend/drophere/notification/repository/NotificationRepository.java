package com.drop.here.backend.drophere.notification.repository;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n where " +
            "n.type = 'NOTIFICATION_PANEL' and " +
            "(n.recipientCompany = :company or n.recipientAccountProfile =:accountProfile) and " +
            "n.readStatus in (:desiredReadStatuses)")
    Page<Notification> findByRecipientCompanyOrRecipientAccountProfileAndReadStatusIn(Company company, AccountProfile accountProfile, Collection<NotificationReadStatus> desiredReadStatuses, Pageable pageable);

    Page<Notification> findByRecipientCustomerAndReadStatusInAndType(Customer customer, Collection<NotificationReadStatus> desiredReadStatuses, NotificationType notificationType, Pageable pageable);

    Optional<Notification> findByIdAndRecipientCompanyOrRecipientAccountProfileAndType(Long notificationId, Company company, AccountProfile accountProfile, NotificationType notificationType);

    Optional<Notification> findByIdAndRecipientCustomerAndType(Long notificationId, Customer customer, NotificationType notificationType);
}
