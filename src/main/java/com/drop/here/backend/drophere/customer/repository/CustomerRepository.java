package com.drop.here.backend.drophere.customer.repository;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAccount(Account principal);

    @Query("select c from Customer c " +
            "join fetch c.image i where " +
            "c.id =:customerId")
    Optional<Customer> findByIdWithImage(Long customerId);
}
