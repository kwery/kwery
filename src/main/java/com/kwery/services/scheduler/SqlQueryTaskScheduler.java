package com.kwery.services.scheduler;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.ONGOING;

public class SqlQueryTaskScheduler implements SchedulerListener {
    protected Logger logger = LoggerFactory.getLogger(SqlQueryTaskScheduler.class);

    private final List<TaskExecutor> ongoingExecutions;

    private final Scheduler scheduler;
    private final SqlQueryExecutionDao sqlQueryExecutionDao;
    private final SqlQueryTaskExecutorListener sqlQueryTaskExecutorListener;
    private final SqlQueryTaskFactory sqlQueryTaskFactory;
    private final Provider<SqlQueryExecution> queryRunExecutionProvider;
    private final SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder;
    private final OneOffSqlQueryTaskSchedulerReaper oneOffSqlQueryTaskSchedulerReaper;

    @Inject
    public SqlQueryTaskScheduler(Scheduler scheduler,
                                 SqlQueryExecutionDao sqlQueryExecutionDao,
                                 SqlQueryTaskFactory sqlQueryTaskFactory,
                                 Provider<SqlQueryExecution> queryRunExecutionProvider,
                                 SqlQueryTaskExecutorListener sqlQueryTaskExecutorListener,
                                 SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder,
                                 OneOffSqlQueryTaskSchedulerReaper oneOffSqlQueryTaskSchedulerReaper,
                                 @Assisted List<TaskExecutor> ongoingExecutions,
                                 @Assisted SqlQuery sqlQuery) {
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.sqlQueryTaskExecutorListener = sqlQueryTaskExecutorListener;
        this.sqlQueryTaskFactory = sqlQueryTaskFactory;
        this.ongoingExecutions = ongoingExecutions;
        this.queryRunExecutionProvider = queryRunExecutionProvider;
        this.scheduler = scheduler;
        this.sqlQueryTaskSchedulerHolder = sqlQueryTaskSchedulerHolder;
        this.oneOffSqlQueryTaskSchedulerReaper = oneOffSqlQueryTaskSchedulerReaper;

        this.scheduler.addSchedulerListener(this);

        if ("".equals(Strings.nullToEmpty(sqlQuery.getCronExpression()))) {
            this.scheduler.start();
            this.scheduler.launch(sqlQueryTaskFactory.create(sqlQuery));
        } else {
            this.scheduler.schedule(sqlQuery.getCronExpression(), sqlQueryTaskFactory.create(sqlQuery));
            this.scheduler.start();
        }
    }

    @Override
    public void taskLaunching(TaskExecutor executor) {
        SqlQuery sqlQuery = ((SqlQueryTask) executor.getTask()).getSqlQuery();
        logger.info("Launching query {} with label {} running on datasource {}", sqlQuery.getQuery(), sqlQuery.getLabel(), sqlQuery.getDatasource().getLabel());

        SqlQueryExecution e = queryRunExecutionProvider.get();
        e.setExecutionId(executor.getGuid());
        e.setExecutionStart(executor.getStartTime());
        e.setSqlQuery(sqlQuery);
        e.setStatus(ONGOING);
        sqlQueryExecutionDao.save(e);

        executor.addTaskExecutorListener(sqlQueryTaskExecutorListener);

        ongoingExecutions.add(executor);

        if (isOneOffExecution(sqlQuery)) {
            oneOffSqlQueryTaskSchedulerReaper.add(new SqlQueryTaskSchedulerExecutorPair(this, executor));
        }
    }

    @Override
    public void taskSucceeded(TaskExecutor executor) {
        SqlQuery sqlQuery = ((SqlQueryTask) executor.getTask()).getSqlQuery();
        logger.info("Query {} with label {} running on datasource {} succeeded", sqlQuery.getQuery(), sqlQuery.getLabel(), sqlQuery.getDatasource().getLabel());
        ongoingExecutions.remove(executor);
        if (isOneOffExecution(sqlQuery)) {
            cleanUp(sqlQuery);
        }
    }

    @Override
    public void taskFailed(TaskExecutor executor, Throwable exception) {
        SqlQuery sqlQuery = ((SqlQueryTask) executor.getTask()).getSqlQuery();
        logger.info("Query {} with label {} running on datasource {} failed due to", sqlQuery.getQuery(), sqlQuery.getLabel(), sqlQuery.getDatasource().getLabel(), exception);
        ongoingExecutions.remove(executor);
        if (isOneOffExecution(sqlQuery)) {
            cleanUp(sqlQuery);
        }
    }

    public List<OngoingSqlQueryTask> ongoingQueryTasks() {
            List<OngoingSqlQueryTask> l = new ArrayList<>(ongoingExecutions.size());

            for (TaskExecutor ongoingExecution : ongoingExecutions) {
                OngoingSqlQueryTask o = new OngoingSqlQueryTask();
                o.setExecutionId(ongoingExecution.getGuid());
                o.setSqlQuery(((SqlQueryTask)ongoingExecution.getTask()).getSqlQuery());
                o.setStartTime(ongoingExecution.getStartTime());
                l.add(o);
            }

            return l;
    }

    public void stopExecution(String executionId) throws SqlQueryExecutionNotFoundException {
        boolean found = false;

        for (TaskExecutor ongoingExecution : ongoingExecutions) {
            if (executionId.equals(ongoingExecution.getGuid())) {
                SqlQueryTask q = (SqlQueryTask)ongoingExecution.getTask();
                logger.info("Stopping query execution {} running on datasource {} with execution id {}", q.getSqlQuery().getQuery(),
                        q.getSqlQuery().getDatasource().getLabel(), executionId);
                ongoingExecution.stop();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new SqlQueryExecutionNotFoundException();
        }
    }

    private void cleanUp(SqlQuery sqlQuery) {
        logger.info("Cleaning up post one off execution");
        sqlQueryTaskSchedulerHolder.remove(sqlQuery.getId(), this);
    }

    private boolean isOneOffExecution(SqlQuery sqlQuery) {
        return "".equals(Strings.nullToEmpty(sqlQuery.getCronExpression()));
    }

    public synchronized void stopScheduler() {
        if (scheduler.isStarted()) {
            logger.info("> Stopping scheduler");
            scheduler.stop();
            logger.info("< Stopping scheduler");
        } else {
            logger.info("Scheduler has already been stopped");
        }
    }

    public boolean hasSchedulerStopped() {
        return !scheduler.isStarted();
    }

/*    public static void main(String[] args) throws InterruptedException, SqlQueryExecutionNotFoundException {
        Datasource datasource = new Datasource();
        datasource.setUrl("localhost");
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setPort(3306);
        datasource.setType(Datasource.Type.MYSQL);
        datasource.setLabel("test");

        SqlQuery queryRun = new SqlQuery();
        queryRun.setCronExpression("* * * * *");
        queryRun.setDatasource(datasource);
        queryRun.setLabel("test");
        queryRun.setQuery("select sleep(86400)");

        SqlQueryTaskScheduler scheduler = new SqlQueryTaskScheduler(new Scheduler());

        scheduler.schedule(queryRun);

        System.out.println("----------------------------------------------------");

        TimeUnit.MINUTES.sleep(2);

        while (true) {
            List<OngoingSqlQueryTask> tasks = scheduler.ongoingQueryTasks();
            for (OngoingSqlQueryTask task : tasks) {
                System.out.println(task.getExecutionId() + " - " + new Date(task.getStartTime()));
                scheduler.stopExecution(task.getExecutionId());
            }
        }
    }*/
}