package com.drop.here.backend.drophere.security.configuration.websocket.authorization;

import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;

public interface WebSocketEndpointAuthorizationService {
    boolean authorize(AccountAuthentication accountAuthentication, String destinationEndpointless);
}