package com.drop.here.backend.drophere.authentication.account.entity;

import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(indexes = @Index(columnList = "mail"),
        uniqueConstraints = @UniqueConstraint(columnNames = "mail"))
public class Account {

    @Id
    private Long id;

    @NotBlank
    private String mail;

    @NotBlank
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountMailStatus accountMailStatus;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime mailActivatedAt;

    // TODO: 03/08/2020 jakeis role?
}