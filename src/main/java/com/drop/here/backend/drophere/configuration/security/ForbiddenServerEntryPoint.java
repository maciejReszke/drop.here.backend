package com.drop.here.backend.drophere.configuration.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class ForbiddenServerEntryPoint extends HttpStatusServerEntryPoint {

    public ForbiddenServerEntryPoint() {
        super(HttpStatus.FORBIDDEN);
    }
}
