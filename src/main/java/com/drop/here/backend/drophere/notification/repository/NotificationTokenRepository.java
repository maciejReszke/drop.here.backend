package com.drop.here.backend.drophere.notification.repository;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {
    Optional<NotificationToken> findByOwnerCompany(Company company);

    Optional<NotificationToken> findByOwnerCustomer(Customer customer);

    Optional<NotificationToken> findByOwnerAccountProfile(AccountProfile accountProfile);
}
