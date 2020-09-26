package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.drop.entity.Drop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {
}
