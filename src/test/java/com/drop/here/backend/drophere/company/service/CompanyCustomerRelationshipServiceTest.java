package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.repository.CompanyCustomerRelationshipRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyCustomerRelationshipServiceTest {

    @InjectMocks
    private CompanyCustomerRelationshipService companyCustomerRelationshipService;

    @Mock
    private CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;

    @Mock
    private CompanyMappingService companyMappingService;

    @Test
    void givenBlockedCustomerWhenIsBlockedThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Customer customer = Customer.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(true);

        //when
        final boolean result = companyCustomerRelationshipService.isBlocked(company, customer);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotBlockedCustomerWhenIsBlockedThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Customer customer = Customer.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(false);

        //when
        final boolean result = companyCustomerRelationshipService.isBlocked(company, customer);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenBlockedCustomerAndBlockingOperationWhenHandleBlockingThenDoNothing() {
        //given
        final boolean block = true;
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(true);

        //when
        companyCustomerRelationshipService.handleCustomerBlocking(block, customer, company);

        //then
        verifyNoMoreInteractions(companyCustomerRelationshipRepository);
    }

    @Test
    void givenNotBlockedCustomerAndBlockingOperationExistingRelationshipWhenHandleBlockingThenUpdate() {
        //given
        final boolean block = true;
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(false);
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
        when(companyCustomerRelationshipRepository.findByCompanyAndCustomer(company, customer))
                .thenReturn(Optional.of(relationship));
        when(companyCustomerRelationshipRepository.save(relationship)).thenReturn(relationship);

        //when
        companyCustomerRelationshipService.handleCustomerBlocking(block, customer, company);

        //then
        verifyNoMoreInteractions(companyCustomerRelationshipRepository);
        assertThat(relationship.getRelationshipStatus()).isEqualTo(CompanyCustomerRelationshipStatus.BLOCKED);
    }

    @Test
    void givenNotBlockedCustomerAndBlockingOperationNotExistingRelationshipWhenHandleBlockingThenCreate() {
        //given
        final boolean block = true;
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(false);
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
        when(companyCustomerRelationshipRepository.findByCompanyAndCustomer(company, customer))
                .thenReturn(Optional.empty());
        when(companyMappingService.createActiveRelationship(customer, company)).thenReturn(relationship);
        when(companyCustomerRelationshipRepository.save(relationship)).thenReturn(relationship);

        //when
        companyCustomerRelationshipService.handleCustomerBlocking(block, customer, company);

        //then
        verifyNoMoreInteractions(companyCustomerRelationshipRepository);
        assertThat(relationship.getRelationshipStatus()).isEqualTo(CompanyCustomerRelationshipStatus.BLOCKED);
    }

    @Test
    void givenNotBlockedCustomerAndUnblockingOperationWhenHandleBlockingThenDoNothing() {
        //given
        final boolean block = false;
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(false);

        //when
        companyCustomerRelationshipService.handleCustomerBlocking(block, customer, company);

        //then
        verifyNoMoreInteractions(companyCustomerRelationshipRepository);
    }

    @Test
    void givenBlockedCustomerAndUnblockingOperationWhenHandleBlockingThenDoNothing() {
        //given
        final boolean block = false;
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerAndRelationshipStatus(company, customer, CompanyCustomerRelationshipStatus.BLOCKED))
                .thenReturn(true);
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        when(companyCustomerRelationshipRepository.findByCompanyAndCustomer(company, customer))
                .thenReturn(Optional.of(relationship));
        when(companyCustomerRelationshipRepository.save(relationship)).thenReturn(relationship);
        //when
        companyCustomerRelationshipService.handleCustomerBlocking(block, customer, company);

        //then
        verifyNoMoreInteractions(companyCustomerRelationshipRepository);
        assertThat(relationship.getRelationshipStatus()).isEqualTo(CompanyCustomerRelationshipStatus.ACTIVE);
    }

    @Test
    void givenExistingRelationshipWhenHasRelationshipThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerId(company, customerId))
                .thenReturn(true);

        //when
        final boolean result = companyCustomerRelationshipService.hasRelationship(company, customerId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotExistingRelationshipWhenHasRelationshipThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(companyCustomerRelationshipRepository.existsByCompanyAndCustomerId(company, customerId))
                .thenReturn(false);

        //when
        final boolean result = companyCustomerRelationshipService.hasRelationship(company, customerId);

        //then
        assertThat(result).isFalse();
    }

}