package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationBuilder {

    public AccountAuthentication buildAuthentication(Account account, PreAuthentication preAuthentication) {
        final List<SimpleGrantedAuthority> privileges = account.getPrivileges().stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
                .collect(Collectors.toList());

        return AccountAuthentication.builder()
                .account(account)
                .authorities(privileges)
                .tokenValidUntil(preAuthentication.getValidUntil())
                .build();
    }
}
