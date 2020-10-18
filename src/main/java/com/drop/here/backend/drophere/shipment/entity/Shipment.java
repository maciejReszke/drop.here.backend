package com.drop.here.backend.drophere.shipment.entity;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
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
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString(exclude = {"customer", "drop", "products", "company"})
@EqualsAndHashCode(exclude = {"customer", "drop", "products", "company"})
@Table(indexes = {
        @Index(columnList = "company_id, status"),
        @Index(columnList = "customer_id, status")
})
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drop_id")
    private Drop drop;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime placedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime compromiseAcceptedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime acceptedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime cancelledAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime cancelRequestedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deliveredAt;

    @NotNull
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShipmentProduct> products;

    @Positive
    @NotNull
    private BigDecimal summarizedAmount;

    private String customerComment;

    private String companyComment;

    @Version
    private Long version;
}
