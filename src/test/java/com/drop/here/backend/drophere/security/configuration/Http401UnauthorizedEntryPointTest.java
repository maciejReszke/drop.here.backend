package com.drop.here.backend.drophere.security.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class Http401UnauthorizedEntryPointTest {
    @InjectMocks
    private Http401UnauthorizedEntryPoint entryPoint;

    @Mock
    private HttpServletResponse response;

    @Test
    void givenResponseWhenCommenceThenSend401() throws IOException {
        //given
        doNothing().when(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        //when
        entryPoint.commence(null, response, null);

        //then
        verifyNoMoreInteractions(response);
    }

}