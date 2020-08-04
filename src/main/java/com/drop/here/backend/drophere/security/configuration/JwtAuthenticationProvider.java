package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.UnauthorizedRestException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AccountService accountService;
    private final AuthenticationBuilder authenticationBuilder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        final PreAuthentication preAuthentication = (PreAuthentication) authentication;
        final String mail = preAuthentication.getMail();
        log.info("Received valid token for account {}", mail);

        final Account account = accountService.findActiveAccountByMailWithRoles(mail)
                .orElseThrow(() -> new UnauthorizedRestException(String.format(
                        "During token authentication didn't find active account %s", mail),
                        RestExceptionStatusCode.JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_ACCOUNT));

        log.info("Token for account {} was valid and account was found", mail);

        return authenticationBuilder.buildAuthentication(account, preAuthentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthentication.class);
    }
}
