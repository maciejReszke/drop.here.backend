package com.drop.here.backend.drophere.company.entity;

import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class CompanyCustomerRelationship {

    @Id
    private String id;

    @NotNull
    private CompanyCustomerRelationshipStatus relationshipStatus;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @NotNull
    @DBRef
    private Customer customer;

    @NotNull
    @DBRef
    private Company company;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdatedAt;

    @Version
    private Long version;

}
