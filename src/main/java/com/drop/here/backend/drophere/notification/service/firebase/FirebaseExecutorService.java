package com.drop.here.backend.drophere.notification.service.firebase;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FirebaseExecutorService {
    Mono<Void> sendAll(List<Message> messages) throws FirebaseMessagingException;

    Mono<Void> initializeApp(FirebaseOptions options);
}
