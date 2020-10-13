package com.drop.here.backend.drophere.order.repository;

import com.drop.here.backend.drophere.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
