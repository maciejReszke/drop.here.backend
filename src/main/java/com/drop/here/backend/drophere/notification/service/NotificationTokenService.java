package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class NotificationTokenService {
    private final NotificationTokenRepository notificationTokenRepository;

    public Optional<String> findByType(Notification notification, NotificationTokenType notificationTokenType) {
        return API.Match(notificationTokenType).of(
                Case($(NotificationTokenType.COMPANY), () -> notificationTokenRepository.findByOwnerCompany(notification.getRecipientCompany())),
                Case($(NotificationTokenType.COMPANY_PROFILE), () -> notificationTokenRepository.findByOwnerAccountProfile(notification.getRecipientAccountProfile())),
                Case($(NotificationTokenType.CUSTOMER), () -> notificationTokenRepository.findByOwnerCustomer(notification.getRecipientCustomer()))
        ).map(NotificationToken::getToken);
    }
}
