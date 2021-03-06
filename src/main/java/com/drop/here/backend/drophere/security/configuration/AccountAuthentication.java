package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder(toBuilder = true)
public class AccountAuthentication implements Authentication {
    private final Account account;
    private final Collection<? extends GrantedAuthority> authorities;
    private final LocalDateTime tokenValidUntil;
    private final AccountProfile profile;
    private final Company company;
    private final Customer customer;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @JsonIgnore
    public Object getCredentials() {
        return null;
    }

    @Override
    @JsonIgnore
    public Object getDetails() {
        return account;
    }

    @Override
    @JsonIgnore
    public Account getPrincipal() {
        return account;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        //Does nothing
    }

    @Override
    public String getName() {
        return account.getMail();
    }

    public LocalDateTime getTokenValidUntil() {
        return tokenValidUntil;
    }

    public AccountProfile getProfile() {
        return profile;
    }

    public boolean hasProfile() {
        return profile != null;
    }

    public Company getCompany() {
        return company;
    }

    public Customer getCustomer() {
        return customer;
    }
}
