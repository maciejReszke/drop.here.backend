package com.drop.here.backend.drophere.notification.repository;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByOwnerAccountProfileAndBroadcastingServiceType(AccountProfile accountProfile, NotificationBroadcastingServiceType notificationBroadcastingServiceType);

    Optional<NotificationToken> findByOwnerCustomerAndBroadcastingServiceType(Customer customer, NotificationBroadcastingServiceType notificationBroadcastingServiceType);

    List<NotificationToken> findByOwnerCustomerInAndBroadcastingServiceType(List<Customer> customers, NotificationBroadcastingServiceType type);

    List<NotificationToken> findByOwnerAccountProfileInAndBroadcastingServiceType(List<AccountProfile> profiles, NotificationBroadcastingServiceType serviceType);
}
