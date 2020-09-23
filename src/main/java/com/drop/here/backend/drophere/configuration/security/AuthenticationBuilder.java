package com.drop.here.backend.drophere.configuration.security;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AuthenticationBuilder {

    public AccountAuthentication buildAuthentication(Account account, PreAuthentication preAuthentication) {
        final List<SimpleGrantedAuthority> privileges = getAccountPrivileges(account);

        return AccountAuthentication.builder()
                .account(account)
                .authorities(privileges)
                .tokenValidUntil(preAuthentication.getValidUntil())
                .company(account.getCompany())
                .customer(account.getCustomer())
                .build();
    }

    private List<SimpleGrantedAuthority> getAccountPrivileges(Account account) {
        return mapPrivileges(account.getPrivileges());
    }

    public AccountAuthentication buildAuthentication(Account account, AccountProfile profile, PreAuthentication preAuthentication) {
        final List<SimpleGrantedAuthority> privileges = Stream.concat(
                getAccountPrivileges(account).stream(),
                getProfilePrivileges(profile).stream()
        )
                .collect(Collectors.toList());

        return AccountAuthentication.builder()
                .account(account)
                .authorities(privileges)
                .tokenValidUntil(preAuthentication.getValidUntil())
                .profile(profile)
                .company(account.getCompany())
                .customer(account.getCustomer())
                .build();
    }

    private List<SimpleGrantedAuthority> getProfilePrivileges(AccountProfile profile) {
        return mapPrivileges(profile.getPrivileges());
    }

    private List<SimpleGrantedAuthority> mapPrivileges(List<Privilege> privileges) {
        return privileges
                .stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
                .collect(Collectors.toList());
    }
}
