package com.drop.here.backend.drophere.configuration.security;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.exception.UnauthorizedRestException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

// TODO MONO:
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final AuthenticationBuilder authenticationBuilder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final PreAuthentication preAuthentication = (PreAuthentication) authentication;
        final String mail = preAuthentication.getMail();
        log.info("Received valid token for account {}", mail);

        return accountService.findActiveAccountByMailWithRoles(mail)
                .switchIfEmpty(Mono.error(() -> new UnauthorizedRestException(String.format(
                        "During token authentication didn't find active account %s", mail),
                        RestExceptionStatusCode.JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_ACCOUNT)))
                .doOnNext(account -> log.info("Token for account {} was valid and account was found", mail))
                .flatMap(account -> buildAuthentication(account, preAuthentication));
    }

    private Mono<Authentication> buildAuthentication(Account account, PreAuthentication preAuthentication) {
        return StringUtils.isBlank(preAuthentication.getProfileUid())
                ? Mono.just(authenticationBuilder.buildAuthentication(account, preAuthentication))
                : authenticateWithProfile(account, preAuthentication);
    }

    private Mono<Authentication> authenticateWithProfile(Account account, PreAuthentication preAuthentication) {
        final String profileUid = preAuthentication.getProfileUid();

        return accountProfileService.findActiveProfile(account, profileUid)
                .switchIfEmpty(Mono.error(() -> new UnauthorizedRestException(String.format(
                        "During token authentication didn't find active profile for account with id %s", profileUid),
                        RestExceptionStatusCode.JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_PROFILE)))
                .doOnNext(accountProfile -> log.info("Account profile {} for account {} was valid and found", profileUid, account.getId()))
                .map(accountProfile -> authenticationBuilder.buildAuthentication(account, accountProfile, preAuthentication));
    }
}
