package com.drop.here.backend.drophere.security.configuration.websocket;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.location.endpoint.LocationWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.messaging.util.matcher.SimpDestinationMessageMatcher;

@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        messages
                .matchers(new SimpDestinationMessageMatcher(WebSocketConfig.WEB_SOCKET_DESTINATION_PREFIX + LocationWebSocketController.ENDPOINT)).hasAnyAuthority(PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE, PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                .matchers(new SimpDestinationMessageMatcher(LocationWebSocketController.ENDPOINT + "/**")).permitAll()
                .matchers(new SimpDestinationMessageMatcher("/**")).denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
