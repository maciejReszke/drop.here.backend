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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(indexes = @Index(columnList = "mail"),
        uniqueConstraints = @UniqueConstraint(columnNames = "mail"))
@ToString(exclude = {"privileges", "company"})
@EqualsAndHashCode(exclude = {"privileges", "company"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String mail;

    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountRegistrationType registrationType;

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
    private LocalDateTime deactivatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime mailActivatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Privilege> privileges;

    @NotNull
    private boolean isAnyProfileRegistered;

    @Version
    private Long version;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "account")
    private Company company;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "account")
    private Customer customer;
}