package com.kwery.tests.util;

import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;

public class CustomIdManufacturer extends IntTypeManufacturerImpl {
    @Override
    public Integer getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
        //TODO - User interface for entities
        if ("id".equals(attributeMetadata.getAttributeName())) {
            int high = TestUtil.DB_START_ID;
            int low = 1;
            return new Random().nextInt((high + 1) - low) + low;
        }

        return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
    }
}
