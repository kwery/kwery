package services.scheduler;

import models.Datasource;
import models.QueryRun;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public interface QueryTaskFactory {
    QueryTask create(QueryRun q, Datasource d, long cancelCheckFrequency);
    default QueryTask create(QueryRun q, Datasource d) {
        return create(q, d, MILLISECONDS.convert(1, MILLISECONDS));
    }
}
