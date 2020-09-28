package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.response.ProductCategoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

        when(productService.findCategories(companyUid)).thenReturn(List.of("category"));

        //when
        final List<ProductCategoryResponse> response = productCategoryService.findAll(companyUid);

        //then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("category");
    }
}