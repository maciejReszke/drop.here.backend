package com.drop.here.backend.drophere.product.entity;

import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"product", "customizations"})
@EqualsAndHashCode(exclude = {"product", "customizations"})
public class ProductCustomizationWrapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String heading;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductCustomizationWrapperType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wrapper")
    private List<ProductCustomization> customizations;
}
