package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.drop.entity.Drop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {
    Optional<Drop> findByIdAndCompanyUid(Long dropId, String companyUid);

    Stream<Drop> findAllByCompanyUidAndNameStartsWith(String companyUid, String name);
}
