package com.drop.here.backend.drophere.company.entity;

import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.Country;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document
public class Company {

    @Id
    private String id;

    @NotBlank
    @Indexed
    private String name;

    @NotBlank
    private String uid;

    @NotNull
    private Country country;

    @NotNull
    private CompanyVisibilityStatus visibilityStatus;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdatedAt;

    @Version
    private Long version;
}

