package com.drop.here.backend.drophere.shipment.dto;

import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class ShipmentResponse {

    @ApiModelProperty(value = "Shipment id", example = "5")
    Long id;

    @ApiModelProperty(value = "Shipment status", example = "PLACED")
    ShipmentStatus status;

    @ApiModelProperty(value = "Drop uid", example = "j123uboi")
    String dropUid;

    @ApiModelProperty(value = "Company name", example = "Glodny maciek")
    String companyName;

    @ApiModelProperty(value = "Company uid", example = "uid123")
    String companyUid;

    @ApiModelProperty(value = "Shipment creation date time", example = "2020-04-06T11:03:32")
    String createdAt;

    @ApiModelProperty(value = "Shipment placement date time", example = "2020-04-06T11:03:32")
    String placedAt;

    @ApiModelProperty(value = "Shipment compromising acceptation date time", example = "2020-04-06T11:03:32")
    String compromiseAcceptedAt;

    @ApiModelProperty(value = "Shipment acceptation date time", example = "2020-04-06T11:03:32")
    String acceptedAt;

    @ApiModelProperty(value = "Shipment cancellation date time", example = "2020-04-06T11:03:32")
    String cancelledAt;

    @ApiModelProperty(value = "Shipment cancellation request at date time (if not automatically cancelled)", example = "2020-04-06T11:03:32")
    String cancelRequestedAt;

    @ApiModelProperty(value = "Shipment delivery confirmation date time", example = "2020-04-06T11:03:32")
    String deliveredAt;

    @ApiModelProperty(value = "Shipment rejection date time", example = "2020-04-06T11:03:32")
    String rejectedAt;

    @ApiModelProperty(value = "Shipment products")
    List<ShipmentProductResponse> products;

    @ApiModelProperty(value = "Shipment summarized amount (price)", example = "55.32")
    BigDecimal summarizedAmount;

    @ApiModelProperty(value = "Shipment customer latest comment", example = "Jestem zniesmaczony państwa zachowaniem")
    String customerComment;

    @ApiModelProperty(value = "Shipment company latest comment", example = "Mamy to gleboko i szeroko")
    String companyComment;

}
