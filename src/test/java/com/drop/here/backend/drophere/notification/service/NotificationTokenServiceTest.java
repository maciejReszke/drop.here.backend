package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTokenServiceTest {
    @InjectMocks
    private NotificationTokenService notificationTokenService;

    @Mock
    private NotificationTokenRepository notificationTokenRepository;

    @Test
    void givenCompanyTypeWhenFindByTypeThenFind() {
        //given
        final NotificationTokenType notificationTokenType = NotificationTokenType.COMPANY;
        final Company company = Company.builder().build();
        final Notification notification = Notification.builder()
                .recipientCompany(company)
                .build();

        final NotificationToken notificationToken = NotificationToken.builder().token("zeberko").build();
        when(notificationTokenRepository.findByOwnerCompany(company)).thenReturn(Optional.of(notificationToken));

        //when
        final Optional<String> result = notificationTokenService.findByType(notification, notificationTokenType);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.orElseThrow()).isEqualTo("zeberko");
    }

    @Test
    void givenCustomerTypeWhenFindByTypeThenFind() {
        //given
        final NotificationTokenType notificationTokenType = NotificationTokenType.CUSTOMER;
        final Customer customer = Customer.builder().build();
        final Notification notification = Notification.builder()
                .recipientCustomer(customer)
                .build();

        final NotificationToken notificationToken = NotificationToken.builder().token("zeberko").build();
        when(notificationTokenRepository.findByOwnerCustomer(customer)).thenReturn(Optional.of(notificationToken));

        //when
        final Optional<String> result = notificationTokenService.findByType(notification, notificationTokenType);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.orElseThrow()).isEqualTo("zeberko");
    }

    @Test
    void givenCompanyProfileTypeWhenFindByTypeThenFind() {
        //given
        final NotificationTokenType notificationTokenType = NotificationTokenType.COMPANY_PROFILE;
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final Notification notification = Notification.builder()
                .recipientAccountProfile(accountProfile)
                .build();

        final NotificationToken notificationToken = NotificationToken.builder().token("zeberko").build();
        when(notificationTokenRepository.findByOwnerAccountProfile(accountProfile)).thenReturn(Optional.of(notificationToken));

        //when
        final Optional<String> result = notificationTokenService.findByType(notification, notificationTokenType);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.orElseThrow()).isEqualTo("zeberko");
    }

    @Test
    void givenNotExistingWhenFindByTypeThenEmpty() {
        //given
        final NotificationTokenType notificationTokenType = NotificationTokenType.COMPANY;
        final Company company = Company.builder().build();
        final Notification notification = Notification.builder()
                .recipientCompany(company)
                .build();

        when(notificationTokenRepository.findByOwnerCompany(company)).thenReturn(Optional.empty());

        //when
        final Optional<String> result = notificationTokenService.findByType(notification, notificationTokenType);

        //then
        assertThat(result).isEmpty();
    }

}