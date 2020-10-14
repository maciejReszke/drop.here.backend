package com.drop.here.backend.drophere.product.dto;

import com.drop.here.backend.drophere.product.entity.Product;
import lombok.Value;

@Value
public class ProductCopy {
    Product original;
    Product copy;
}
