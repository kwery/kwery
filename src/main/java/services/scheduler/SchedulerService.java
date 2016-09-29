package services.scheduler;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dao.DatasourceDao;
import dao.QueryRunDao;
import dtos.QueryRunDto;
import models.Datasource;
import models.QueryRun;
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
    protected QueryRunDao queryRunDao;

    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    protected QueryTaskSchedulerFactory queryTaskSchedulerFactory;

    @Inject
    protected Provider<QueryRun> queryRunProvider;

    protected Map<Integer, QueryTaskScheduler> queryRunSchedulerMap = new HashMap<>();

    //TODO Rollback on error
    public void schedule(QueryRunDto dto) {
        Datasource datasource = datasourceDao.getById(dto.getDatasourceId());
        QueryRun model = toModel(dto, datasource);
        queryRunDao.save(model);
        schedule(model);
    }

    public void schedule(QueryRun queryRun) {
        QueryTaskScheduler queryTaskScheduler = queryTaskSchedulerFactory.create(new CopyOnWriteArrayList<>(), queryRun);
        queryRunSchedulerMap.put(queryRun.getId(), queryTaskScheduler);
    }

    public List<OngoingQueryTask> ongoingQueryTasks(Integer queryRunId) {
        return queryRunSchedulerMap.get(queryRunId).ongoingQueryTasks();
    }

    public void stopExecution(int queryRunId, String taskExecutionId) {
        queryRunSchedulerMap.get(queryRunId).stopExecution(taskExecutionId);
    }

    public QueryRun toModel(QueryRunDto dto, Datasource datasource) {
        QueryRun q = queryRunProvider.get();
        q.setQuery(dto.getQuery());
        q.setCronExpression(dto.getCronExpression());
        q.setLabel(dto.getLabel());
        q.setDatasource(datasource);
        return q;
    }

    @Start
    public void scheduleAllQueries() {
        logger.info("Scheduling all queries");
        queryRunDao.getAll().forEach(this::schedule);
    }

    @Dispose
    public void shutdownSchedulers() {
        logger.info("Stopping all schedulers");
        for (QueryTaskScheduler queryTaskScheduler : queryRunSchedulerMap.values()) {
            queryTaskScheduler.stopScheduler();
        }
    }

    @VisibleForTesting
    public Map<Integer, QueryTaskScheduler> getQueryRunSchedulerMap() {
        return queryRunSchedulerMap;
    }

    public void setQueryRunDao(QueryRunDao queryRunDao) {
        this.queryRunDao = queryRunDao;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }

    public void setQueryRunProvider(Provider<QueryRun> queryRunProvider) {
        this.queryRunProvider = queryRunProvider;
    }
}
