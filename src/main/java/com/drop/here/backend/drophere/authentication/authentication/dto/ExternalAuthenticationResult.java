package com.drop.here.backend.drophere.authentication.authentication.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ExternalAuthenticationResult {

    String email;

    String firstName;

    String lastName;

    byte[] image;
}
