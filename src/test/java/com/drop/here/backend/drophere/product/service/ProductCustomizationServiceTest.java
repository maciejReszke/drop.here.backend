package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationServiceTest {
    @InjectMocks
    private ProductCustomizationService productCustomizationService;

    @Mock
    private ProductCustomizationWrapperRepository productCustomizationWrapperRepository;


    @Test
    void givenProductAndCopyWhenCreateReadOnlyCopyThenCreate(){
        //given
        final Product oldProduct = Product.builder().id(5L).build();
        final Product newProduct = Product.builder().id(6L).build();

        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().id(5L).product(oldProduct)
                .build();
        final ProductCustomization customization = ProductCustomization.builder().id(5L).wrapper(wrapper).build();
        wrapper.setCustomizations(Set.of(customization));
        when(productCustomizationWrapperRepository.findByProductWithCustomizations(oldProduct)).thenReturn(List.of(wrapper));

        //when
        final List<ProductCustomizationWrapper> result = productCustomizationService.createReadOnlyCopies(oldProduct, newProduct);

        //then
        final ProductCustomizationWrapper newWrapper = result.get(0);
        final ProductCustomization newCustomization = newWrapper.getCustomizations().stream().findFirst().orElseThrow();
        assertThat(newWrapper.getId()).isNull();
        assertThat(newWrapper.getProduct()).isEqualTo(newProduct);
        assertThat(newWrapper.getCustomizations()).hasSize(1);
        assertThat(newCustomization.getId()).isNull();
        assertThat(newCustomization.getWrapper()).isEqualTo(newWrapper);
    }

}