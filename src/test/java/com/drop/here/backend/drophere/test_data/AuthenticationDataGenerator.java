package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class AuthenticationDataGenerator {

    public AccountAuthentication accountAuthentication(Account account) {
        return AccountAuthentication
                .builder()
                .authorities(List.of(new SimpleGrantedAuthority("authority")))
                .account(account)
                .tokenValidUntil(LocalDateTime.now())
                .company(account.getCompany())
                .customer(account.getCustomer())
                .build();
    }

    public AccountAuthentication accountAuthenticationWithProfile(Account account, AccountProfile accountProfile) {
        return accountAuthentication(account)
                .toBuilder()
                .profile(accountProfile)
                .build();
    }
}
