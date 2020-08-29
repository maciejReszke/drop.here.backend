package com.drop.here.backend.drophere.authentication.authentication.service.base;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationExecutiveService {
    private final JwtService jwtService;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    public LoginResponse successLogin(Account account) {
        final TokenResponse tokenResponse = jwtService.createToken(account);
        log.info("Logging account with id {}", account.getId());
        return new LoginResponse(tokenResponse.getToken(),
                tokenResponse.getValidUntil().format(DATE_TIME_FORMAT)
        );
    }

    public LoginResponse successLogin(Account account, AccountProfile profile) {
        final TokenResponse tokenResponse = jwtService.createToken(account, profile);
        log.info("Logging account with id {} and profile uid {}", account.getId(), profile.getProfileUid());
        return new LoginResponse(tokenResponse.getToken(),
                tokenResponse.getValidUntil().format(DATE_TIME_FORMAT)
        );
    }

    private List<String> toRoles(Collection<? extends GrantedAuthority> privileges) {
        return privileges.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // TODO: 29/08/2020 czy ma comapny, czy ma customera
    public AuthenticationResponse getAuthenticationInfo(AccountAuthentication accountAuthentication) {
        return accountAuthentication.hasProfile()
                ? getWithProfileAuthenticationInfo(accountAuthentication)
                : getBaseAuthenticationInfo(accountAuthentication);
    }

    private AuthenticationResponse getWithProfileAuthenticationInfo(AccountAuthentication accountAuthentication) {
        final AccountProfile profile = accountAuthentication.getProfile();
        return getBaseAuthenticationInfo(accountAuthentication)
                .toBuilder()
                .hasProfile(true)
                .loggedOnProfile(true)
                .profileUid(profile.getProfileUid())
                .profileFirstName(profile.getFirstName())
                .profileLastName(profile.getLastName())
                .profileType(profile.getProfileType())
                .build();
    }

    private AuthenticationResponse getBaseAuthenticationInfo(AccountAuthentication accountAuthentication) {
        final Account account = accountAuthentication.getPrincipal();
        return AuthenticationResponse.builder()
                .accountId(account.getId())
                .accountStatus(account.getAccountStatus())
                .accountType(account.getAccountType())
                .mail(account.getMail())
                .roles(toRoles(accountAuthentication.getAuthorities()))
                .tokenValidUntil(accountAuthentication.getTokenValidUntil().format(DATE_TIME_FORMAT))
                .loggedOnProfile(false)
                .hasProfile(account.isAnyProfileRegistered())
                .build();
    }
}
