package com.drop.here.backend.drophere.security.configuration.websocket.authorization;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketSubscribingAuthorizationServiceTest {

    @InjectMocks
    private WebSocketSubscribingAuthorizationService webSocketSubscribingAuthorizationService;

    @Mock
    private SellerLocationWebSocketEndpointAuthorizationService sellerLocationWebSocketEndpointAuthorizationService;

    @Test
    void givenSuccessLocationEndpointWhenAuthorizeThenAuthorize() {
        //given
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final String destination = "/locations/123";

        when(sellerLocationWebSocketEndpointAuthorizationService.authorize(accountAuthentication, "123")).thenReturn(true);

        //when
        webSocketSubscribingAuthorizationService.authorize(accountAuthentication, destination);

        //then
        verifyNoMoreInteractions(sellerLocationWebSocketEndpointAuthorizationService);
    }

    @Test
    void givenFailureLocationEndpointWhenAuthorizeThenAuthorize() {
        //given
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final String destination = "/locations/123";

        when(sellerLocationWebSocketEndpointAuthorizationService.authorize(accountAuthentication, "123")).thenReturn(false);

        //when
        final Throwable throwable = catchThrowable(() -> webSocketSubscribingAuthorizationService.authorize(accountAuthentication, destination));

        //then
        assertThat(throwable).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void givenStrangeEndpointWhenAuthorizeThenAccessDeniedException() {
        //given
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(Account.builder().build())
                .build();
        final String destination = "/kaka/123";

        //when
        final Throwable throwable = catchThrowable(() -> webSocketSubscribingAuthorizationService.authorize(accountAuthentication, destination));

        //then
        assertThat(throwable).isInstanceOf(AccessDeniedException.class);
    }

}