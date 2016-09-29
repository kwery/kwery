package services.scheduler;

import models.QueryRun;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public interface QueryTaskFactory {
    QueryTask create(QueryRun queryRun, long cancelCheckFrequency);
    default QueryTask create(QueryRun queryRun) {
        return create(queryRun, MILLISECONDS.convert(1, MILLISECONDS));
    }
}
