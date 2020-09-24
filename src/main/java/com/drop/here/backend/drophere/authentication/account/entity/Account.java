package com.drop.here.backend.drophere.authentication.account.entity;

import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountRegistrationType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder(toBuilder = true)
public class Account {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String mail;

    private String password;

    @NotNull
    private AccountRegistrationType registrationType;

    @NotNull
    private AccountType accountType;

    @NotNull
    private AccountStatus accountStatus;

    @NotNull
    private AccountMailStatus accountMailStatus;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deactivatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime mailActivatedAt;

    @Valid
    @NotNull
    private List<@Valid Privilege> privileges;

    @NotNull
    private boolean isAnyProfileRegistered;

    @Version
    private Long version;

    @DBRef
    private Company company;

    @DBRef
    private Customer customer;

    @Valid
    @NotNull
    private List<@Valid AccountProfile> profiles;
}