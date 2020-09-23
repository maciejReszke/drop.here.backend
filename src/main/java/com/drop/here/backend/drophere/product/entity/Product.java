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
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// TODO: 23/09/2020 customizacje rezaem
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString(exclude = {"unit", "company", "image"})
@EqualsAndHashCode(exclude = {"unit", "company", "image"})
@Table(indexes = @Index(columnList = "category"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private ProductUnit unit;

    @NotBlank
    private String unitName;

    @NotNull
    @PositiveOrZero
    private BigDecimal unitFraction;

    @NotNull
    @Enumerated(EnumType.STRING)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private List<ProductCustomizationWrapper> customizationWrappers;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "image_id")
    private Image image;
}
