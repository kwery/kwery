package com.kwery.services.scheduler;

import com.kwery.models.SqlQueryModel;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public interface SqlQueryTaskFactory {
    SqlQueryTask create(SqlQueryModel sqlQuery, long cancelCheckFrequency);
    default SqlQueryTask create(SqlQueryModel sqlQuery) {
        return create(sqlQuery, MILLISECONDS.convert(1, MILLISECONDS));
    }
}
