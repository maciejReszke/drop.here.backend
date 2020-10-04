package com.drop.here.backend.drophere.location.endpoint;

import com.drop.here.backend.drophere.location.dto.CurrentLocation;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LocationWebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public static final String ENDPOINT = "/locations";

    @MessageMapping(ENDPOINT)
    public void publishLocation(CurrentLocation currentLocation, AccountAuthentication accountAuthentication) {
        simpMessagingTemplate.convertAndSend(ENDPOINT + "/" + accountAuthentication.getProfile().getProfileUid(), currentLocation);
    }
}
