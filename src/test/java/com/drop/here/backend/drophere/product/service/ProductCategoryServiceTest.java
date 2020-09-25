package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.response.ProductCategoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @InjectMocks
    private ProductCategoryService productCategoryService;

    @Mock
    private ProductService productService;

    @Test
    void whenFindAllThenMapAndGet() {
        //given
        final String companyUid = "companyUid";

        when(productService.findCategories(companyUid)).thenReturn(Flux.just("category"));

        //when
        final Flux<ProductCategoryResponse> result = productCategoryService.findAll(companyUid);

        //then
        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getName()).isEqualTo("category"))
                .verifyComplete();
    }
}