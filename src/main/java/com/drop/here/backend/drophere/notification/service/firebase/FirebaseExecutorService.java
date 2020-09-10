package com.drop.here.backend.drophere.notification.service.firebase;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import java.util.List;

public interface FirebaseExecutorService {
    void sendAll(List<Message> messages) throws FirebaseMessagingException;

    void initializeApp(FirebaseOptions options);
}
