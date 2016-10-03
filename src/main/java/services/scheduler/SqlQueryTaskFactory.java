package services.scheduler;

import models.SqlQuery;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public interface SqlQueryTaskFactory {
    SqlQueryTask create(SqlQuery sqlQuery, long cancelCheckFrequency);
    default SqlQueryTask create(SqlQuery sqlQuery) {
        return create(sqlQuery, MILLISECONDS.convert(1, MILLISECONDS));
    }
}
