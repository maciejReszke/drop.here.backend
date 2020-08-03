package com.drop.here.backend.drophere.authentication.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${authentication.jwt.validInMinutes}")
    private int validInMinutes;

    @Value("${authentication.jwt.secret}")
    private String secret;

    private Algorithm algorithm;

    @PostConstruct
    void prepareAlgorithm() {
        this.algorithm = Algorithm.HMAC512(secret.getBytes());
    }

    // TODO: 03/08/2020 privilegs
    public TokenResponse createToken(Account account) {
        final LocalDateTime validUntil = LocalDateTime.now().plusMinutes(validInMinutes);
        final String jwt = JWT.create()
                .withAudience()
                .withExpiresAt(Date.from(validUntil.atZone(ZoneId.systemDefault()).toInstant()))
                .withSubject(account.getMail())
                .sign(algorithm);
        return new TokenResponse(jwt, validUntil);
    }
}
