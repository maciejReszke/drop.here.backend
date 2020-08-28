package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.drop.entity.DropMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropMembershipRepository extends JpaRepository<DropMembership, Long> {
}
