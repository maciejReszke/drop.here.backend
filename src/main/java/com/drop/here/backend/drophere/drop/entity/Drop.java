package com.drop.here.backend.drophere.drop.entity;

import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@ToString(exclude = "company")
@EqualsAndHashCode(exclude = "company")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "uid"}))
public class Drop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String uid;

    @NotNull
    private boolean hidden;

    @NotNull
    private boolean requiresPassword;

    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DropLocationType locationType;

    @NotNull
    private boolean requiresAccept;

    private Double xCoordinate;

    private Double yCoordinate;

    private Integer estimatedRadiusMeters;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdatedAt;
}
