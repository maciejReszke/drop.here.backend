package com.drop.here.backend.drophere.product.entity;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// TODO: 23/09/2020 ogarnac dbrefy
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"unit", "company", "image"})
@EqualsAndHashCode(exclude = {"unit", "company", "image"})
@Document
public class Product {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    @Indexed
    private String category;

    @NotNull
    private ProductUnit unit;

    @NotNull
    @PositiveOrZero
    private BigDecimal unitFraction;

    @NotNull
    private ProductAvailabilityStatus availabilityStatus;

    @NotNull
    @Positive
    private BigDecimal price;

    private String description;

    @Version
    private Long version;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdatedAt;

    @NotNull
    @DBRef
    private Company company;

    @NotNull
    @Valid
    private List<@Valid ProductCustomizationWrapper> customizationWrappers;

    @DBRef
    private Image image;
}
