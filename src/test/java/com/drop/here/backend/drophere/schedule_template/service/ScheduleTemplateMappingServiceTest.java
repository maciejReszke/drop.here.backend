package com.drop.here.backend.drophere.schedule_template.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.service.ProductSearchingService;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplateProduct;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.ScheduleTemplateDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleTemplateMappingServiceTest {

    @InjectMocks
    private ScheduleTemplateMappingService scheduleTemplateMappingService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductSearchingService productSearchingService;

    @Test
    void givenRequestWhenToScheduleTemplateThenMap() {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateDataGenerator.request(1);
        final Company company = CompanyDataGenerator.company(1, null, null);

        final Product product = Product.builder().build();
        when(productService.getProduct(any(), any())).thenReturn(product);

        //when
        final ScheduleTemplate response = scheduleTemplateMappingService.toScheduleTemplate(scheduleTemplateManagementRequest, company);

        //then
        assertThat(response.getCompany()).isEqualTo(company);
        assertThat(response.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getName()).isEqualTo(scheduleTemplateManagementRequest.getName());
        assertThat(response.getScheduleTemplateProducts()).hasSize(2);
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .getAmount()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(0).getAmount());
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .getPrice()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(0).getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .getProduct()).isEqualTo(product);
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .isLimitedAmount()).isTrue();
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .getAmount()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(1).getAmount());
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .getPrice()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(1).getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .getProduct()).isEqualTo(product);
        assertThat(response.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .isLimitedAmount()).isTrue();
    }

    @Test
    void givenRequestAndTemplateWhenUpdateScheduleTemplateThenUpdate() {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateDataGenerator.request(1);
        final Company company = CompanyDataGenerator.company(1, null, null);

        final Product product = Product.builder().build();
        final ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder().build();
        when(productService.getProduct(any(), any())).thenReturn(product);
        scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(0).setLimitedAmount(false);

        //when
        scheduleTemplateMappingService.updateScheduleTemplate(scheduleTemplate, scheduleTemplateManagementRequest, company);

        //then
        assertThat(scheduleTemplate.getCompany()).isNull();
        assertThat(scheduleTemplate.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(scheduleTemplate.getCreatedAt()).isNull();
        assertThat(scheduleTemplate.getName()).isEqualTo(scheduleTemplateManagementRequest.getName());
        assertThat(scheduleTemplate.getScheduleTemplateProducts()).hasSize(2);
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .getAmount()).isZero();
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .isLimitedAmount()).isFalse();
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .getPrice()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(0).getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow()
                .getProduct()).isEqualTo(product);
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .getAmount()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(1).getAmount());
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .getPrice()).isEqualTo(scheduleTemplateManagementRequest.getScheduleTemplateProducts().get(1).getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .getProduct()).isEqualTo(product);
        assertThat(scheduleTemplate.getScheduleTemplateProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow()
                .isLimitedAmount()).isTrue();
    }


    @Test
    void givenScheduleTemplateWhenToTemplateResponseThenMap() {
        //given
        final Company company = CompanyDataGenerator.company(1, null, null);
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setId(5L);
        final ScheduleTemplateProduct scheduleTemplateProduct = ScheduleTemplateProduct.builder()
                .price(BigDecimal.valueOf(15))
                .amount(15)
                .limitedAmount(true)
                .orderNum(5)
                .product(Product.builder().id(5L).build())
                .build();
        scheduleTemplate.setScheduleTemplateProducts(Set.of(scheduleTemplateProduct));
        final ProductResponse productResponse = ProductResponse.builder().id(5L).build();
        when(productSearchingService.findProducts(List.of(5L))).thenReturn(List.of(productResponse));
        //when
        final ScheduleTemplateResponse response = scheduleTemplateMappingService.toTemplateResponse(scheduleTemplate);

        //then
        assertThat(response.getName()).isEqualTo(scheduleTemplate.getName());
        assertThat(response.getProductsAmount()).isEqualTo(1);
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getScheduleTemplateProducts()).hasSize(1);
        assertThat(response.getScheduleTemplateProducts().get(0).getAmount()).isEqualTo(scheduleTemplateProduct.getAmount());
        assertThat(response.getScheduleTemplateProducts().get(0).getPrice()).isEqualTo(scheduleTemplateProduct.getPrice());
        assertThat(response.getScheduleTemplateProducts().get(0).getProductResponse()).isEqualTo(productResponse);
        assertThat(response.getScheduleTemplateProducts().get(0).isLimitedAmount()).isEqualTo(scheduleTemplateProduct.isLimitedAmount());
    }

}