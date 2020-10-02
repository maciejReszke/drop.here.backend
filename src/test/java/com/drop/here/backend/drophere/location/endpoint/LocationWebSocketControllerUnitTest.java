package com.drop.here.backend.drophere.location.endpoint;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.location.dto.CurrentLocation;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationWebSocketControllerUnitTest {

    @InjectMocks
    private LocationWebSocketController locationWebSocketController;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void givenMessageAndAuthenticationWhenPublishLocationThenPublish(){
        //given
        final CurrentLocation currentLocation = new CurrentLocation(3.3, 4.4);
        final Account account = AccountDataGenerator.customerAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);

        doNothing().when(simpMessagingTemplate).convertAndSend("/locations/" + accountProfile.getProfileUid(), currentLocation);
        //when
        locationWebSocketController.publishLocation(currentLocation, accountAuthentication);

        //then
        verifyNoMoreInteractions(simpMessagingTemplate);
    }

}