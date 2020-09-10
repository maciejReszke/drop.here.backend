package com.drop.here.backend.drophere.authentication.token;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TokenResponse {
    String token;
    LocalDateTime validUntil;
}
