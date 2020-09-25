package com.drop.here.backend.drophere.spot.entity;

import com.drop.here.backend.drophere.company.entity.Company;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document
public class Spot {

    @Id
    private String id;

    @NotBlank
    @Indexed
    private String name;

    private String description;

    @NotBlank
    @Indexed
    private String uid;

    @NotNull
    private boolean hidden;

    @NotNull
    private boolean requiresPassword;

    private String password;

    @NotNull
    private boolean requiresAccept;

    @NotNull
    private Double xCoordinate;

    @NotNull
    private Double yCoordinate;

    @NotNull
    private Integer estimatedRadiusMeters;

    @NotNull
    @DBRef
    private Company company;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdatedAt;

    @Version
    private Long version;
}
