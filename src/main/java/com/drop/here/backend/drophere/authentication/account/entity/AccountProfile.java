package com.drop.here.backend.drophere.authentication.account.entity;

import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AccountProfile {

    @NotBlank
    private String profileUid;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    @NotNull
    private AccountProfileStatus status;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deactivatedAt;

    @NotNull
    @Valid
    private List<@Valid Privilege> privileges;

    @NotNull
    private AccountProfileType profileType;
}
