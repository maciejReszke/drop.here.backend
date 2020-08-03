package com.drop.here.backend.drophere.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

// TODO: 01/08/2020 get endpoint
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(indexes = {
        @Index(unique = true, columnList = "name"),
        @Index(unique = true, columnList = "mobilePrefix")
})
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String mobilePrefix;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CountryStatus countryStatus;

    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activatedAt;
}
