package com.drop.here.backend.drophere.spot.entity;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class SpotMembership {

    @Id
    private String id;

    @NotNull
    private SpotMembershipStatus membershipStatus;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DBRef
    @NotNull
    private Customer customer;

    @NotNull
    @DBRef
    private Spot spot;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdatedAt;

    @Version
    private Long version;

    @NotNull
    private boolean receiveNotification;
}
