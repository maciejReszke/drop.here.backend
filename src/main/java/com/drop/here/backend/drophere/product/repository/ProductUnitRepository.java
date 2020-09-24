package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.ProductUnit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductUnitRepository extends ReactiveMongoRepository<ProductUnit, Long> {
    Mono<ProductUnit> findByName(String name);
}
