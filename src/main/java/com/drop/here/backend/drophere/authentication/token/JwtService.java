package com.drop.here.backend.drophere.authentication.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.security.configuration.PreAuthentication;
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

    @Value("${authentication.jwt.issuer}")
    private String issuer;

    @Value("${authentication.jwt.secret}")
    private String secret;

    private Algorithm algorithm;

    @PostConstruct
    void prepareAlgorithm() {
        this.algorithm = Algorithm.HMAC512(secret.getBytes());
    }

    public TokenResponse createToken(Account account) {
        final LocalDateTime validUntil = LocalDateTime.now().plusMinutes(validInMinutes);
        final String jwt = JWT.create()
                .withAudience(issuer)
                .withExpiresAt(Date.from(validUntil.atZone(ZoneId.systemDefault()).toInstant()))
                .withSubject(account.getMail())
                .withIssuer(issuer)
                .sign(algorithm);
        return new TokenResponse(jwt, validUntil);
    }

    // TODO: 03/08/2020 Dodac jeszcze jaki profil uzywa!
    public PreAuthentication decodeToken(String token) {
        final JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(issuer)
                .build();

        final DecodedJWT decodedJwt = verifier.verify(token);

        return new PreAuthentication(decodedJwt.getSubject(), dateToLocalDateTime(decodedJwt));
    }

    private LocalDateTime dateToLocalDateTime(DecodedJWT decodedJwt) {
        return decodedJwt
                .getExpiresAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
