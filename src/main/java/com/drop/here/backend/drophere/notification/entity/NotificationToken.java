package com.drop.here.backend.drophere.notification.entity;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationToken {

    // TODO: 25/09/2020 do tabel customer i accountprofile
    @NotBlank
    private String token;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationTokenType tokenType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationBroadcastingServiceType broadcastingServiceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_customer_id")
    private Customer ownerCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_profile_id")
    private AccountProfile ownerAccountProfile;
}
