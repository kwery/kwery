package services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import dao.QueryRunExecutionDao;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.QueryRun;
import models.QueryRunExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static models.QueryRunExecution.Status.ONGOING;

public class QueryTaskScheduler implements SchedulerListener {
    protected Logger logger = LoggerFactory.getLogger(QueryTaskScheduler.class);

    private final List<TaskExecutor> ongoingExecutions;

    private final Scheduler scheduler;
    private final QueryRunExecutionDao queryRunExecutionDao;
    private final QueryTaskExecutorListener queryTaskExecutorListener;
    private final QueryTaskFactory queryTaskFactory;
    private final Provider<QueryRunExecution> queryRunExecutionProvider;

    @Inject
    public QueryTaskScheduler(Scheduler scheduler, QueryRunExecutionDao queryRunExecutionDao, QueryTaskFactory queryTaskFactory,
                              Provider<QueryRunExecution> queryRunExecutionProvider, QueryTaskExecutorListener queryTaskExecutorListener,
                              @Assisted List<TaskExecutor> ongoingExecutions, @Assisted QueryRun queryRun) {
        this.queryRunExecutionDao = queryRunExecutionDao;
        this.queryTaskExecutorListener = queryTaskExecutorListener;
        this.queryTaskFactory = queryTaskFactory;
        this.ongoingExecutions = ongoingExecutions;
        this.queryRunExecutionProvider = queryRunExecutionProvider;
        this.scheduler = scheduler;
        this.scheduler.addSchedulerListener(this);
        this.scheduler.schedule(queryRun.getCronExpression(), queryTaskFactory.create(queryRun));
        this.scheduler.start();
    }

    @Override
    public void taskLaunching(TaskExecutor executor) {
        QueryRun queryRun = ((QueryTask) executor.getTask()).getQueryRun();
        logger.info("Starting query {} with label {} running on datasource {}", queryRun.getQuery(), queryRun.getLabel(), queryRun.getDatasource().getLabel());

        QueryRunExecution e = queryRunExecutionProvider.get();
        e.setExecutionId(executor.getGuid());
        e.setExecutionStart(executor.getStartTime());
        e.setQueryRun(queryRun);
        e.setStatus(ONGOING);
        queryRunExecutionDao.save(e);

        executor.addTaskExecutorListener(queryTaskExecutorListener);

        ongoingExecutions.add(executor);
    }

    @Override
    public void taskSucceeded(TaskExecutor executor) {
        ongoingExecutions.remove(executor);
    }

    @Override
    public void taskFailed(TaskExecutor executor, Throwable exception) {
        QueryRun queryRun = ((QueryTask) executor.getTask()).getQueryRun();
        logger.info("Query {} with label {} running on datasource {} failed due to", queryRun.getQuery(), queryRun.getLabel(), queryRun.getDatasource().getLabel(), exception);
        ongoingExecutions.remove(executor);
    }

    public List<OngoingQueryTask> ongoingQueryTasks() {
            List<OngoingQueryTask> l = new ArrayList<>(ongoingExecutions.size());

            for (TaskExecutor ongoingExecution : ongoingExecutions) {
                OngoingQueryTask o = new OngoingQueryTask();
                o.setExecutionId(ongoingExecution.getGuid());
                o.setQueryRun(((QueryTask)ongoingExecution.getTask()).getQueryRun());
                o.setStartTime(ongoingExecution.getStartTime());
                l.add(o);
            }

            return l;
    }

    public void stopExecution(String executionId) {
        for (TaskExecutor ongoingExecution : ongoingExecutions) {
            if (executionId.equals(ongoingExecution.getGuid())) {
                QueryTask q = (QueryTask)ongoingExecution.getTask();
                logger.info("Stopping query execution {} running on datasource {} with execution id {}", q.getQueryRun().getQuery(),
                        q.getQueryRun().getDatasource().getLabel(), executionId);
                ongoingExecution.stop();
                break;
            }
        }
    }

    public void stopScheduler() {
        scheduler.stop();
    }

    public boolean hasSchedulerStopped() {
        return !scheduler.isStarted();
    }

    public static void main(String[] args) throws InterruptedException {
/*        Datasource datasource = new Datasource();
        datasource.setUrl("localhost");
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setPort(3306);
        datasource.setType(Datasource.Type.MYSQL);
        datasource.setLabel("test");

        QueryRun queryRun = new QueryRun();
        queryRun.setCronExpression("* * * * *");
        queryRun.setDatasource(datasource);
        queryRun.setLabel("test");
        queryRun.setQuery("select sleep(86400)");

        QueryTaskScheduler scheduler = new QueryTaskScheduler(new Scheduler());

        scheduler.schedule(queryRun);

        System.out.println("----------------------------------------------------");

        TimeUnit.MINUTES.sleep(2);

        while (true) {
            List<OngoingQueryTask> tasks = scheduler.ongoingQueryTasks();
            for (OngoingQueryTask task : tasks) {
                System.out.println(task.getExecutionId() + " - " + new Date(task.getStartTime()));
                scheduler.stopExecution(task.getExecutionId());
            }
        }*/
    }
}
