package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.properties.Property;
import com.drop.here.backend.drophere.properties.PropertyService;
import com.drop.here.backend.drophere.properties.PropertyType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseInitializationServiceTest {
    @InjectMocks
    private FirebaseInitializationService firebaseInitializationService;

    @Mock
    private FirebaseExecutorService firebaseExecutorService;

    @Mock
    private PropertyService propertyService;

    @Test
    void givenInitializedAppWhenInitializeThenDoNothing() throws IOException, IllegalAccessException {
        //given
        ((AtomicBoolean) (FieldUtils.getField(FirebaseInitializationService.class, "INITIALIZED", true).get(1)))
                .set(true);
        //when
        firebaseInitializationService.initialize();

        //then
        verifyNoMoreInteractions(propertyService);
    }

    @Test
    void givenNotInitializedAppSuccessInitializationWhenInitializeThenDoNothing() throws IOException, IllegalAccessException {
        //given
        ((AtomicBoolean) (FieldUtils.getField(FirebaseInitializationService.class, "INITIALIZED", true).get(1)))
                .set(false);

        final String json = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"drop--here\",\n" +
                "  \"private_key_id\": \"asd\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAq7BFUpkGp3+LQmlQ\\nYx2eqzDV+xeG8kx/sQFV18S5JhzGeIJNA72wSeukEPojtqUyX2J0CciPBh7eqclQ\\n2zpAswIDAQABAkAgisq4+zRdrzkwH1ITV1vpytnkO/NiHcnePQiOW0VUybPyHoGM\\n/jf75C5xET7ZQpBe5kx5VHsPZj0CBb3b+wSRAiEA2mPWCBytosIU/ODRfq6EiV04\\nlt6waE7I2uSPqIC20LcCIQDJQYIHQII+3YaPqyhGgqMexuuuGx+lDKD6/Fu/JwPb\\n5QIhAKthiYcYKlL9h8bjDsQhZDUACPasjzdsDEdq8inDyLOFAiEAmCr/tZwA3qeA\\nZoBzI10DGPIuoKXBd3nk/eBxPkaxlEECIQCNymjsoI7GldtujVnr1qT+3yedLfHK\\nsrDVjIT3LsvTqw==\\n-----END PRIVATE KEY-----\",\n" +
                "  \"client_email\": \"asd\",\n" +
                "  \"client_id\": \"asd\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-4a4wv%40drop--here.iam.gserviceaccount.com\"\n" +
                "}\n";
        when(propertyService.getProperty(PropertyType.GOOGLE_CREDENTIALS_CONFIGURATION)).thenReturn(Property.builder().value(json).build());
        doNothing().when(firebaseExecutorService).initializeApp(any());

        //when
        firebaseInitializationService.initialize();

        //then
        assertThat(((AtomicBoolean) (FieldUtils.getField(FirebaseInitializationService.class, "INITIALIZED", true).get(1))))
                .isTrue();
    }

    @Test
    void givenNotInitializedAppFailureInitializationWhenInitializeThenThrowException() throws IOException, IllegalAccessException {
        //given
        ((AtomicBoolean) (FieldUtils.getField(FirebaseInitializationService.class, "INITIALIZED", true).get(1)))
                .set(false);

        final String json = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"drop--here\",\n" +
                "  \"private_key_id\": \"asd\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAq7BFUpkGp3+LQmlQ\\nYx2eqzDV+xeG8kx/sQFV18S5JhzGeIJNA72wSeukEPojtqUyX2J0CciPBh7eqclQ\\n2zpAswIDAQABAkAgisq4+zRdrzkwH1ITV1vpytnkO/NiHcnePQiOW0VUybPyHoGM\\n/jf75C5xET7ZQpBe5kx5VHsPZj0CBb3b+wSRAiEA2mPWCBytosIU/ODRfq6EiV04\\nlt6waE7I2uSPqIC20LcCIQDJQYIHQII+3YaPqyhGgqMexuuuGx+lDKD6/Fu/JwPb\\n5QIhAKthiYcYKlL9h8bjDsQhZDUACPasjzdsDEdq8inDyLOFAiEAmCr/tZwA3qeA\\nZoBzI10DGPIuoKXBd3nk/eBxPkaxlEECIQCNymjsoI7GldtujVnr1qT+3yedLfHK\\nsrDVjIT3LsvTqw==\\n-----END PRIVATE KEY-----\",\n" +
                "  \"client_email\": \"asd\",\n" +
                "  \"client_id\": \"asd\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-4a4wv%40drop--here.iam.gserviceaccount.com\"\n" +
                "}\n";
        when(propertyService.getProperty(PropertyType.GOOGLE_CREDENTIALS_CONFIGURATION)).thenReturn(Property.builder().value(json).build());
        doThrow(new RuntimeException()).when(firebaseExecutorService).initializeApp(any());

        //when
        final Throwable throwable = catchThrowable(() -> firebaseInitializationService.initialize());

        //then
        assertThat(throwable).isNotNull();
        assertThat(((AtomicBoolean) (FieldUtils.getField(FirebaseInitializationService.class, "INITIALIZED", true).get(1))))
                .isFalse();
    }

}