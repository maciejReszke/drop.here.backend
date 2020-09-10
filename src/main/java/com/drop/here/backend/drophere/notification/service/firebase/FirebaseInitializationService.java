package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseInitializationService {
    private final FirebaseMappingService firebaseMappingService;
    private final FirebaseExecutorService firebaseExecutorService;
    private final ObjectMapper objectMapper;

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    @Value("${googlecredentials.databaseUrl}")
    private String databaseUrl;

    public void initialize() throws IOException {
        if (INITIALIZED.get()) {
            return;
        }
        log.info("Initializing firebase app on database url {}", databaseUrl);
        final FirebaseOptions options = prepareFirebaseOptions();
        firebaseExecutorService.initializeApp(options);
        log.info("Successfully initialized firebase app on database url {}", databaseUrl);
        INITIALIZED.set(true);
    }

    private FirebaseOptions prepareFirebaseOptions() throws IOException {
        final GoogleCredentialsRequest credentialsRequest = firebaseMappingService.prepareCredentialsRequest();
        final String json = objectMapper.writeValueAsString(credentialsRequest);
        final ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(json.getBytes());
        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .setDatabaseUrl(databaseUrl)
                .build();
    }
}
