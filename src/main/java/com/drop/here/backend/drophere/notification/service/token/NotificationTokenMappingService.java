package com.drop.here.backend.drophere.notification.service.token;


import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

@Service
public class NotificationTokenMappingService {

    public NotificationToken toNotificationToken(AccountAuthentication accountAuthentication, NotificationTokenManagementRequest notificationTokenManagementRequest) {
        return NotificationToken.builder()
                .broadcastingServiceType(Try.ofSupplier(() -> NotificationBroadcastingServiceType.valueOf(notificationTokenManagementRequest.getBroadcastingServiceType()))
                        .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                                "During creating notification token invalid broadcasting service type %s was given", notificationTokenManagementRequest.getBroadcastingServiceType()),
                                RestExceptionStatusCode.CREATE_NOTIFICATION_TOKEN_INVALID_BROADCASTING_TYPE)))
                .tokenType(accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY ? NotificationTokenType.PROFILE : NotificationTokenType.CUSTOMER)
                .token(notificationTokenManagementRequest.getToken())
                .ownerCustomer(accountAuthentication.getCustomer())
                .ownerAccountProfile(accountAuthentication.getProfile())
                .build();
    }
}
