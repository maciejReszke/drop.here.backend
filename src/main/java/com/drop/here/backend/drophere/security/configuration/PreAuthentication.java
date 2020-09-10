package com.drop.here.backend.drophere.security.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PreAuthentication implements Authentication {
    private final String mail;
    private final String profileUid;
    private final LocalDateTime validUntil;

    public static PreAuthentication withProfile(String mail, String profileUid, LocalDateTime validUntil) {
        return new PreAuthentication(mail, profileUid, validUntil);
    }

    public static PreAuthentication withoutProfile(String mail, LocalDateTime validUntil) {
        return new PreAuthentication(mail, null, validUntil);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}
