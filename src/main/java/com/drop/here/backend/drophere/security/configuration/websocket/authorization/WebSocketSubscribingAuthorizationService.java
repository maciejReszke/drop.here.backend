package com.drop.here.backend.drophere.security.configuration.websocket.authorization;

import com.drop.here.backend.drophere.location.endpoint.LocationWebSocketController;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class WebSocketSubscribingAuthorizationService {
    private final SellerLocationWebSocketEndpointAuthorizationService sellerLocationAuthorizationService;

    public void authorize(AccountAuthentication user, String destination) {
        final boolean authorize = API.Match(destination).of(
                Case($(endpoint -> endpoint.startsWith(LocationWebSocketController.ENDPOINT)),
                        () -> sellerLocationAuthorizationService.authorize(user, removeEndpoint(destination, LocationWebSocketController.ENDPOINT))),
                Case($(), () -> false));

        if (!authorize) {
            throw new AccessDeniedException(String.format("Forbidden %s", destination));
        }
    }

    private String removeEndpoint(String destination, String endpoint) {
        return destination.replaceFirst(endpoint, "").replaceFirst("/", "");
    }


}
