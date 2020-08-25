package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import org.springframework.stereotype.Service;

// TODO: 25/08/2020
@Service
public class ProductCustomizationService {

    // TODO: 25/08/2020
    public ProductCustomizationWrapper createCustomizations(Product product, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        return null;
    }

    // TODO: 25/08/2020 nazwa?
    // TODO: 25/08/2020  powinno poprzednie dezaktywowac lub usunac jezeli moze i utworzyc nowe z nowym id
    // TODO: 25/08/2020 tak naprawde czemu nie moglo by usuwac? w koncu i tak bedziemy w zamowieniu wszystkie informacje przesylac o aktualnych cenach itd
    public void deleteCustomization(Product product, Long customizationId) {

    }

    // TODO: 25/08/2020
    // TODO: 25/08/2020  powinno poprzednie dezaktywowac lub usunac jezeli moze i utworzyc nowe z nowym id
    public ProductCustomizationWrapper updateCustomization(Product productId, Long customizationId, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        return null;
    }
}
