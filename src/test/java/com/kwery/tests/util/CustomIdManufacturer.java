package com.kwery.tests.util;

import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;

import java.lang.reflect.Type;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;

public class CustomIdManufacturer extends IntTypeManufacturerImpl {
    @Override
    public Integer getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
        //TODO - User interface for entities
        if ("id".equals(attributeMetadata.getAttributeName())) {
            return dbId();
        }

        return super.getType(strategy, attributeMetadata, genericTypesArgumentsMap);
    }
}
