package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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
                tokenResponse.getValidUntil().format(DATE_TIME_FORMAT),
                account.getAccountType()
        );
    }
}
