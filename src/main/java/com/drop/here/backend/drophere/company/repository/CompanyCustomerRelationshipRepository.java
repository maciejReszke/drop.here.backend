package com.drop.here.backend.drophere.company.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyCustomerRelationshipRepository extends JpaRepository<CompanyCustomerRelationship, Long> {
    boolean existsByCompanyAndCustomerAndRelationshipStatus(Company company, Customer customer, CompanyCustomerRelationshipStatus blocked);

    Optional<CompanyCustomerRelationship> findByCompanyAndCustomer(Company company, Customer customer);

    boolean existsByCompanyAndCustomerId(Company company, Long customerId);
}
