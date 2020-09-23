package com.drop.here.backend.drophere.notification.repository;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

// TODO MONO:
@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, Long> {

    // TODO: 23/09/2020
    @Query("select n from Notification n where " +
            "(n.recipientCompany = :company or n.recipientAccountProfile =:accountProfile) and " +
            "n.readStatus in (:desiredReadStatuses)")
    Page<Notification> findByRecipientCompanyOrRecipientAccountProfileAndReadStatusIn(Company company, AccountProfile accountProfile, Collection<NotificationReadStatus> desiredReadStatuses, Pageable pageable);

    Page<Notification> findByRecipientCustomerAndReadStatusIn(Customer customer, Collection<NotificationReadStatus> desiredReadStatuses, Pageable pageable);

    Optional<Notification> findByIdAndRecipientCompanyOrRecipientAccountProfile(Long notificationId, Company company, AccountProfile accountProfile);

    Optional<Notification> findByIdAndRecipientCustomer(Long notificationId, Customer customer);
}
