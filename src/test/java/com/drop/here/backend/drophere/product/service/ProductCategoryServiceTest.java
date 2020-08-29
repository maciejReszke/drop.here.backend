package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.product.dto.response.ProductCategoryResponse;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.repository.ProductCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @InjectMocks
    private ProductCategoryService productCategoryService;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Test
    void givenExistingProductCategoryWhenGetByNameThenGet() {
        //given
        final String name = "name";
        final ProductCategory category = ProductCategory.builder().build();
        when(productCategoryRepository.findByName(name)).thenReturn(Optional.of(category));

        //when
        final ProductCategory result = productCategoryService.getByName(name);

        //then
        assertThat(result).isEqualTo(category);
    }

    @Test
    void givenNotExistingProductCategoryWhenGetByNameThenException() {
        //given
        final String name = "name";
        when(productCategoryRepository.findByName(name)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productCategoryService.getByName(name));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void whenFindAllThenMapAndGet() {
        //given
        when(productCategoryRepository.findAll(Sort.by("name"))).thenReturn(List.of(ProductCategory
                .builder().name("category").build()));

        //when
        final List<ProductCategoryResponse> response = productCategoryService.findAll();

        //then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("category");
    }
}