package com.kwery.services.job.parameterised;

public interface SqlQueryParameterExtractorFactory {
    SqlQueryParameterExtractor create(String query);
}
