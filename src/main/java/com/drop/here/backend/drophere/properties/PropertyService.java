package com.drop.here.backend.drophere.properties;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public Property getProperty(PropertyType propertyType) {
        return propertyRepository.findByPropertyType(propertyType)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Property by type %s was not found", propertyType)));
    }
}
