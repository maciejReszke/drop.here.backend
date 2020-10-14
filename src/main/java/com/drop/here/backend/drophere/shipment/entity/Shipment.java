package com.drop.here.backend.drophere.shipment.entity;

import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

// TODO: 13/10/2020 - zmienic trzeba na np. parcel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;
}
