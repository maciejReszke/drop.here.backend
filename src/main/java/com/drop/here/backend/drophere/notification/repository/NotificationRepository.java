package com.drop.here.backend.drophere.notification.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientCompanyAndReadStatusIn(Company company, Collection<NotificationReadStatus> desiredReadStatuses, Pageable pageable);

    Page<Notification> findByRecipientCustomerAndReadStatusIn(Customer customer, Collection<NotificationReadStatus> desiredReadStatuses, Pageable pageable);

    Optional<Notification> findByIdAndRecipientCompany(Long notificationId, Company company);

    Optional<Notification> findByIdAndRecipientCustomer(Long notificationId, Customer customer);
}
