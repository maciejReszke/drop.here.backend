package com.drop.here.backend.drophere.notification.service.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO MONO:
@Service
public class FirebaseLiveExecutorService implements FirebaseExecutorService {
    @Override
    public void sendAll(List<Message> messages) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().sendAll(messages);
    }

    @Override
    public void initializeApp(FirebaseOptions options) {
        FirebaseApp.initializeApp(options);
    }
}
