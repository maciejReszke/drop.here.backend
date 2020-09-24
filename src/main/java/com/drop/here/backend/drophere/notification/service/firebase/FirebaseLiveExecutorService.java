package com.drop.here.backend.drophere.notification.service.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class FirebaseLiveExecutorService implements FirebaseExecutorService {
    @Override
    public Mono<Void> sendAll(List<Message> messages) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().sendAll(messages);
        return Mono.empty();
    }

    @Override
    public Mono<Void> initializeApp(FirebaseOptions options) {
        FirebaseApp.initializeApp(options);
        return Mono.empty();
    }
}
