package com.drop.here.backend.drophere.authentication.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.configuration.security.PreAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

// TODO: 23/09/2020
// TODO MONO:
@Service
public class JwtService {

    @Value("${authentication.jwt.valid_in_minutes}")
    private int validInMinutes;

    @Value("${authentication.jwt.issuer}")
    private String issuer;

    @Value("${authentication.jwt.profile_claim_name}")
    private String profileClaimName;

    @Value("${authentication.jwt.secret}")
    private String secret;

    private Algorithm algorithm;

    @PostConstruct
    void prepareAlgorithm() {
        this.algorithm = Algorithm.HMAC512(secret.getBytes());
    }

    public TokenResponse createToken(Account account, AccountProfile profile) {
        final LocalDateTime validUntil = LocalDateTime.now().plusMinutes(validInMinutes);
        final String jwt = baseJwt(account, validUntil)
                .withClaim(profileClaimName, profile.getProfileUid())
                .sign(algorithm);
        return new TokenResponse(jwt, validUntil);
    }

    private JWTCreator.Builder baseJwt(Account account, LocalDateTime validUntil) {
        return JWT.create()
                .withAudience(issuer)
                .withExpiresAt(Date.from(validUntil.atZone(ZoneId.systemDefault()).toInstant()))
                .withSubject(account.getMail())
                .withIssuer(issuer);
    }

    public TokenResponse createToken(Account account) {
        final LocalDateTime validUntil = LocalDateTime.now().plusMinutes(validInMinutes);
        final String jwt = baseJwt(account, validUntil)
                .sign(algorithm);
        return new TokenResponse(jwt, validUntil);
    }

    public PreAuthentication decodeToken(String token) {
        final JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(issuer)
                .build();

        final DecodedJWT decodedJwt = verifier.verify(token);

        final Claim profileClaim = decodedJwt.getClaim(profileClaimName);

        return profileClaim.isNull()
                ? PreAuthentication.withoutProfile(decodedJwt.getSubject(), dateToLocalDateTime(decodedJwt))
                : PreAuthentication.withProfile(decodedJwt.getSubject(), profileClaim.asString(), dateToLocalDateTime(decodedJwt));
    }

    private LocalDateTime dateToLocalDateTime(DecodedJWT decodedJwt) {
        return decodedJwt
                .getExpiresAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
