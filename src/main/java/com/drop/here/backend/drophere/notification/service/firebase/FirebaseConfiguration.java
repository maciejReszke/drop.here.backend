package com.drop.here.backend.drophere.notification.service.firebase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfiguration {

    @Bean
    public FirebaseExecutorService firebaseExecutorService(@Value("${firebase.enabled}") boolean firebaseEnabled,
                                                           FirebaseLiveExecutorService firebaseLiveExecutorService,
                                                           MockedFirebaseExecutorService mockedFirebaseExecutorService) {
        return firebaseEnabled
                ? firebaseLiveExecutorService
                : mockedFirebaseExecutorService;
    }
}
