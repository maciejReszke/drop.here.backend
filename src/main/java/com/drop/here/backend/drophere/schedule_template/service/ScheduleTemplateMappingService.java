package com.drop.here.backend.drophere.schedule_template.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.service.ProductSearchingService;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateProductRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateProductResponse;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplateProduct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleTemplateMappingService {
    private final ProductService productService;
    private final ProductSearchingService productSearchingService;

    public ScheduleTemplate toScheduleTemplate(ScheduleTemplateManagementRequest scheduleTemplateManagementRequest, Company company) {
        final ScheduleTemplate template = ScheduleTemplate.builder()
                .createdAt(LocalDateTime.now())
                .company(company)
                .build();
        updateScheduleTemplate(template, scheduleTemplateManagementRequest, company);
        return template;
    }

    public void updateScheduleTemplate(ScheduleTemplate scheduleTemplate, ScheduleTemplateManagementRequest scheduleTemplateManagementRequest, Company company) {
        scheduleTemplate.setLastUpdatedAt(LocalDateTime.now());
        scheduleTemplate.setName(scheduleTemplateManagementRequest.getName().trim());
        scheduleTemplate.setScheduleTemplateProducts(buildScheduleTemplateProducts(scheduleTemplateManagementRequest, scheduleTemplate, company));
    }

    private Set<ScheduleTemplateProduct> buildScheduleTemplateProducts(ScheduleTemplateManagementRequest scheduleTemplateManagementRequest, ScheduleTemplate scheduleTemplate, Company company) {
        final AtomicInteger counter = new AtomicInteger(0);
        return CollectionUtils.emptyIfNull(scheduleTemplateManagementRequest.getScheduleTemplateProducts())
                .stream()
                .map(scheduleTemplateProductRequest -> buildScheduleTemplateProduct(scheduleTemplateProductRequest, scheduleTemplate, company, counter.incrementAndGet()))
                .collect(Collectors.toSet());
    }

    private ScheduleTemplateProduct buildScheduleTemplateProduct(ScheduleTemplateProductRequest scheduleTemplateProductRequest, ScheduleTemplate scheduleTemplate, Company company, int counter) {
        return ScheduleTemplateProduct.builder()
                .amount(scheduleTemplateProductRequest.isLimitedAmount() && scheduleTemplateProductRequest.getAmount() != null ? scheduleTemplateProductRequest.getAmount() : 0)
                .limitedAmount(scheduleTemplateProductRequest.isLimitedAmount())
                .orderNum(counter)
                .price(scheduleTemplateProductRequest.getPrice().setScale(2, RoundingMode.DOWN))
                .product(productService.getProduct(scheduleTemplateProductRequest.getProductId(), company.getUid()))
                .scheduleTemplate(scheduleTemplate)
                .build();
    }

    public ScheduleTemplateResponse toTemplateResponse(ScheduleTemplate scheduleTemplate) {
        return ScheduleTemplateResponse.builder()
                .name(scheduleTemplate.getName())
                .scheduleTemplateProducts(toTemplateProductsResponse(scheduleTemplate.getScheduleTemplateProducts()))
                .id(scheduleTemplate.getId())
                .productsAmount(scheduleTemplate.getScheduleTemplateProducts().size())
                .build();
    }

    private List<ScheduleTemplateProductResponse> toTemplateProductsResponse(Set<ScheduleTemplateProduct> scheduleTemplateProducts) {
        final List<Long> productsIds = scheduleTemplateProducts.stream()
                .map(ScheduleTemplateProduct::getProduct)
                .map(Product::getId)
                .collect(Collectors.toList());

        final List<ProductResponse> products = productSearchingService.findProducts(productsIds);

        return scheduleTemplateProducts.stream()
                .sorted(Comparator.comparing(ScheduleTemplateProduct::getOrderNum, Integer::compareTo))
                .map(scheduleTemplateProduct -> toTemplateProductResponse(scheduleTemplateProduct, findProductForTemplateProduct(products, scheduleTemplateProduct)))
                .collect(Collectors.toList());
    }

    private ProductResponse findProductForTemplateProduct(List<ProductResponse> products, ScheduleTemplateProduct scheduleTemplateProduct) {
        return products.stream()
                .filter(productResponse -> productResponse.getId().equals(scheduleTemplateProduct.getProduct().getId()))
                .findFirst()
                .orElseThrow();
    }

    private ScheduleTemplateProductResponse toTemplateProductResponse(ScheduleTemplateProduct scheduleTemplateProduct, ProductResponse productResponse) {
        return ScheduleTemplateProductResponse.builder()
                .amount(scheduleTemplateProduct.getAmount())
                .limitedAmount(scheduleTemplateProduct.isLimitedAmount())
                .price(scheduleTemplateProduct.getPrice())
                .productResponse(productResponse)
                .build();
    }
}
