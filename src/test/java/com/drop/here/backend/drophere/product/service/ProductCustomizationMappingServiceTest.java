package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationMappingServiceTest {

    @InjectMocks
    private ProductCustomizationMappingService productCustomizationMappingService;

    @Test
    void givenRequestAndProductWhenToCustomizationWrapperThenMap() {
        //given
        final ProductUnit unit = ProductDataGenerator.unit(1);
        final ProductCategory category = ProductDataGenerator.category(1);
        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, category, unit, company);
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);

        //when
        final ProductCustomizationWrapper wrapper = productCustomizationMappingService.toCustomizationWrapper(product, request);

        //then
        assertThat(wrapper.getCustomizations()).hasSize(2);
        assertThat(wrapper.getCustomizations().stream().filter(t -> t.getOrderNum().equals(1)).findFirst().orElseThrow().getPrice()).isEqualTo(request.getCustomizations().get(0).getPrice());
        assertThat(wrapper.getCustomizations().stream().filter(t -> t.getOrderNum().equals(1)).findFirst().orElseThrow().getValue()).isEqualTo(request.getCustomizations().get(0).getValue());
        assertThat(wrapper.getCustomizations().stream().filter(t -> t.getOrderNum().equals(1)).findFirst().orElseThrow().getWrapper()).isEqualTo(wrapper);
        assertThat(wrapper.getCustomizations().stream().filter(t -> t.getOrderNum().equals(2)).findFirst().orElseThrow().getPrice()).isEqualTo(request.getCustomizations().get(1).getPrice());
        assertThat(wrapper.getCustomizations().stream().filter(t -> t.getOrderNum().equals(2)).findFirst().orElseThrow().getValue()).isEqualTo(request.getCustomizations().get(1).getValue());
        assertThat(wrapper.getCustomizations().stream().filter(t -> t.getOrderNum().equals(2)).findFirst().orElseThrow().getWrapper()).isEqualTo(wrapper);
        assertThat(wrapper.getHeading()).isEqualTo(request.getHeading());
        assertThat(wrapper.getType()).isEqualTo(ProductCustomizationWrapperType.SINGLE);
        assertThat(wrapper.getProduct()).isEqualTo(product);
    }

}