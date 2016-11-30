package com.kwery.services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class SchedulerService {
    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Inject
    protected SqlQueryDao sqlQueryDao;

    @Inject
    protected QueryTaskSchedulerFactory queryTaskSchedulerFactory;

    @Inject
    protected Provider<SqlQueryModel> queryRunProvider;

    @Inject
    protected SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder;

    public void schedule(SqlQueryModel sqlQuery) {
        SqlQueryTaskScheduler sqlQueryTaskScheduler = queryTaskSchedulerFactory.create(new CopyOnWriteArrayList<>(), sqlQuery);
        sqlQueryTaskSchedulerHolder.add(sqlQuery.getId(), sqlQueryTaskScheduler);
    }

    public void stopScheduler(int sqlQueryId) {
        sqlQueryTaskSchedulerHolder.get(sqlQueryId).forEach(SqlQueryTaskScheduler::stopScheduler);
        sqlQueryTaskSchedulerHolder.remove(sqlQueryId);
    }

    public List<OngoingSqlQueryTask> ongoingQueryTasks(Integer sqlQueryId) {
        List<OngoingSqlQueryTask> ongoingSqlQueryTasks = new LinkedList<>();

        for (SqlQueryTaskScheduler sqlQueryTaskScheduler : sqlQueryTaskSchedulerHolder.get(sqlQueryId)) {
            ongoingSqlQueryTasks.addAll(sqlQueryTaskScheduler.ongoingQueryTasks());
        }

        return ongoingSqlQueryTasks;
    }

    public void stopExecution(int sqlQueryId, String sqlQueryExecutionId) throws SqlQueryExecutionNotFoundException {
        Collection<SqlQueryTaskScheduler> sqlQueryTaskSchedulers = sqlQueryTaskSchedulerHolder.get(sqlQueryId);

        if (sqlQueryTaskSchedulers == null) {
            throw new SqlQueryExecutionNotFoundException();
        }

        //TODO - This is ugly, fix this
        boolean found = false;
        for (SqlQueryTaskScheduler sqlQueryTaskScheduler : sqlQueryTaskSchedulers) {
            try {
                sqlQueryTaskScheduler.stopExecution(sqlQueryExecutionId);
                found = true;
                break;
            } catch (SqlQueryExecutionNotFoundException e) {
                //Ignore
            }
        }

        if (!found) {
            throw new SqlQueryExecutionNotFoundException();
        }
    }

    public SqlQueryModel toModel(SqlQueryDto dto, Datasource datasource) {
        SqlQueryModel q = queryRunProvider.get();
        q.setQuery(dto.getQuery());
        q.setCronExpression(dto.getCronExpression());
        q.setLabel(dto.getLabel());
        q.setDatasource(datasource);
        return q;
    }

    @Start
    public void scheduleAllQueries() {
        List<SqlQueryModel> allWithSchedule = sqlQueryDao.getAllWithSchedule();
        logger.info("Scheduling {} queries with schedule", allWithSchedule.size());
        allWithSchedule.forEach(this::schedule);
    }

    @Dispose
    public void shutdownSchedulers() {
        logger.info("Stopping all schedulers");

        Collection<SqlQueryTaskScheduler> all = sqlQueryTaskSchedulerHolder.all();

        logger.info("Stopping {} schedulers", all.size());

        for (SqlQueryTaskScheduler sqlQueryTaskScheduler : all) {
            sqlQueryTaskScheduler.stopScheduler();
        }
    }

    public void setSqlQueryDao(SqlQueryDao sqlQueryDao) {
        this.sqlQueryDao = sqlQueryDao;
    }

    public void setQueryRunProvider(Provider<SqlQueryModel> queryRunProvider) {
        this.queryRunProvider = queryRunProvider;
    }

    public void setSqlQueryTaskSchedulerHolder(SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder) {
        this.sqlQueryTaskSchedulerHolder = sqlQueryTaskSchedulerHolder;
    }
}
