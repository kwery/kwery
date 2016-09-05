package services.scheduler;

import models.Datasource;
import models.QueryRun;

public interface QueryTaskFactory {
    QueryTask create(QueryRun q, Datasource d);
}
