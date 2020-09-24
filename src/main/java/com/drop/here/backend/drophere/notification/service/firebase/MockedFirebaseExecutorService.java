package com.drop.here.backend.drophere.notification.service.firebase;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class MockedFirebaseExecutorService implements FirebaseExecutorService {

    @Override
    public Mono<Void> sendAll(List<Message> messages) {
        log.info("Mocked firebase executor service: sendAll(List<Message> messages)");
        return Mono.empty();
    }

    @Override
    public Mono<Void> initializeApp(FirebaseOptions options) {
        log.info("Mocked firebase executor service: initializeApp(FirebaseOptions options)");
        return Mono.empty();
    }
}
