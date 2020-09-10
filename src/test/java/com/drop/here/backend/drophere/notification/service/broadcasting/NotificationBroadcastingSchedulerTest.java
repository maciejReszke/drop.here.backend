package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationJobRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.NotificationDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationBroadcastingSchedulerTest extends IntegrationBaseClass {

    @Autowired
    private NotificationBroadcastingScheduler notificationBroadcastingScheduler;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    private Customer customer;
    private NotificationToken notificationToken;

    @BeforeEach
    void prepare() {
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        notificationToken = notificationTokenRepository.save(NotificationToken.builder().broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE).tokenType(NotificationTokenType.CUSTOMER).token("token123").build());
    }

    @AfterEach
    void cleanUp() {
        notificationJobRepository.deleteAll();
        notificationTokenRepository.deleteAll();
        notificationRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void givenNotSentNotificationsWhenSendThenSend() {
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notificationRepository.save(notification);
        notificationJobRepository.save(NotificationJob.builder().createdAt(LocalDateTime.now()).notification(notification).notificationToken(notificationToken).build());

        //when
        notificationBroadcastingScheduler.broadcastNotifications();

        //then
        assertThat(notificationJobRepository.findAll()).isEmpty();
    }

}