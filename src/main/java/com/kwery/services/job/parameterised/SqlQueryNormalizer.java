package com.kwery.services.job.parameterised;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SqlQueryNormalizer {
    protected final String query;

    @Inject
    public SqlQueryNormalizer(@Assisted String query) {
        this.query = query;
    }

    public String normalise() {
        return query.replaceAll(SqlQueryParameterExtractor.PARAMETER_REG_EX, "?");
    }
}
