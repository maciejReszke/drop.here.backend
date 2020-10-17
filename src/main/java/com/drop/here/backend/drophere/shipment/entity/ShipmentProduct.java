package com.drop.here.backend.drophere.shipment.entity;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString(exclude = {"product", "shipment", "routeProduct", "customizations"})
@EqualsAndHashCode(exclude = {"product", "shipment", "routeProduct", "customizations"})
public class ShipmentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_product_id")
    private RouteProduct routeProduct;

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipmentProduct", cascade = CascadeType.ALL)
    private Set<ShipmentProductCustomization> customizations;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @NotNull
    @PositiveOrZero
    private Integer orderNum;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    @NotNull
    @Positive
    private BigDecimal customizationsPrice;

    @NotNull
    @Positive
    private BigDecimal summarizedPrice;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;


}
