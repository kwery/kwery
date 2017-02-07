package com.kwery.tests.util;

import com.kwery.models.Datasource;
import com.kwery.models.Datasource.Type;

import static com.kwery.models.Datasource.Type.REDSHIFT;

public class RedshiftDocker extends PostgreSqlDocker {
    protected Type type = REDSHIFT;

    @Override
    public Datasource.Type getType() {
        return type;
    }
}
