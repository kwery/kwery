package com.kwery.services.job.parameterised;

public interface SqlQueryNormalizerFactory {
    SqlQueryNormalizer create(String sqlQuery);
}
