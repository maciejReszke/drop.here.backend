package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.drop.entity.Drop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {
    Optional<Drop> findByIdAndCompanyUid(Long dropId, String companyUid);

    Optional<Drop> findByUidAndCompanyUid(String dropUid, String companyUid);

    List<Drop> findAllByCompanyUidAndNameStartsWith(String companyUid, String name);
}
