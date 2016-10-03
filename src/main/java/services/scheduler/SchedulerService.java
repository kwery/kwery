package services.scheduler;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dtos.SqlQueryDto;
import models.Datasource;
import models.SqlQuery;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class SchedulerService {
    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Inject
    protected SqlQueryDao sqlQueryDao;

    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    protected QueryTaskSchedulerFactory queryTaskSchedulerFactory;

    @Inject
    protected Provider<SqlQuery> queryRunProvider;

    protected Map<Integer, SqlQueryTaskScheduler> queryRunSchedulerMap = new HashMap<>();

    //TODO Rollback on error
    public void schedule(SqlQueryDto dto) {
        Datasource datasource = datasourceDao.getById(dto.getDatasourceId());
        SqlQuery model = toModel(dto, datasource);
        sqlQueryDao.save(model);
        schedule(model);
    }

    public void schedule(SqlQuery sqlQuery) {
        SqlQueryTaskScheduler sqlQueryTaskScheduler = queryTaskSchedulerFactory.create(new CopyOnWriteArrayList<>(), sqlQuery);
        queryRunSchedulerMap.put(sqlQuery.getId(), sqlQueryTaskScheduler);
    }

    public List<OngoingSqlQueryTask> ongoingQueryTasks(Integer sqlQueryId) {
        return queryRunSchedulerMap.get(sqlQueryId).ongoingQueryTasks();
    }

    public void stopExecution(int sqlQueryId, String sqlQueryExecutionId) throws SqlQueryExecutionNotFoundException {
        SqlQueryTaskScheduler sqlQueryTaskScheduler = queryRunSchedulerMap.get(sqlQueryId);
        if (sqlQueryTaskScheduler == null) {
            throw new SqlQueryExecutionNotFoundException();
        }
        sqlQueryTaskScheduler.stopExecution(sqlQueryExecutionId);
    }

    public SqlQuery toModel(SqlQueryDto dto, Datasource datasource) {
        SqlQuery q = queryRunProvider.get();
        q.setQuery(dto.getQuery());
        q.setCronExpression(dto.getCronExpression());
        q.setLabel(dto.getLabel());
        q.setDatasource(datasource);
        return q;
    }

    @Start
    public void scheduleAllQueries() {
        logger.info("Scheduling all queries");
        sqlQueryDao.getAll().forEach(this::schedule);
    }

    @Dispose
    public void shutdownSchedulers() {
        logger.info("Stopping all schedulers");
        for (SqlQueryTaskScheduler sqlQueryTaskScheduler : queryRunSchedulerMap.values()) {
            sqlQueryTaskScheduler.stopScheduler();
        }
    }

    @VisibleForTesting
    public Map<Integer, SqlQueryTaskScheduler> getQueryRunSchedulerMap() {
        return queryRunSchedulerMap;
    }

    public void setSqlQueryDao(SqlQueryDao sqlQueryDao) {
        this.sqlQueryDao = sqlQueryDao;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }

    public void setQueryRunProvider(Provider<SqlQuery> queryRunProvider) {
        this.queryRunProvider = queryRunProvider;
    }
}
