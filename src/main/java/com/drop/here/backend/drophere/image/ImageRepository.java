package com.drop.here.backend.drophere.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// TODO MONO:
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
