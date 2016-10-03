package services;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import dao.SqlQueryExecutionDao;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import services.scheduler.OngoingSqlQueryTask;
import services.scheduler.SqlQueryExecutionNotFoundException;
import services.scheduler.SqlQueryTask;
import services.scheduler.SqlQueryTaskExecutorListener;
import services.scheduler.SqlQueryTaskFactory;
import services.scheduler.SqlQueryTaskScheduler;

import java.util.LinkedList;
import java.util.List;

import static models.SqlQueryExecution.Status.ONGOING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

@RunWith(MockitoJUnitRunner.class)
public class SqlQueryTaskSchedulerTest {
    @Mock
    private Scheduler scheduler;
    @Mock
    private SqlQueryExecutionDao sqlQueryExecutionDao;
    @Mock
    private SqlQueryTaskFactory sqlQueryTaskFactory;
    @Mock
    private Provider<SqlQueryExecution> provider;
    @Mock
    private SqlQueryTaskExecutorListener sqlQueryTaskExecutorListener;
    @Mock
    private TaskExecutor taskExecutor;
    @Mock
    private SqlQueryTask sqlQueryTask;

    private SqlQuery sqlQuery;
    private Datasource datasource;
    private SqlQueryExecution sqlQueryExecution;
    private String taskExecutorGuid = "foo";
    private long executorStartTime = 1l;

    @Before
    public void setUpQueryTaskSchedulerTest() {
        sqlQuery = queryRun();
        datasource = datasource();
        sqlQuery.setDatasource(datasource);
        sqlQueryExecution = new SqlQueryExecution();

        doNothing().when(scheduler).addSchedulerListener(any());
        when(scheduler.schedule(anyString(), any(Task.class))).thenReturn("");
        doNothing().when(scheduler).start();
        when(taskExecutor.getTask()).thenReturn(sqlQueryTask);
        when(sqlQueryTask.getSqlQuery()).thenReturn(sqlQuery);
        when(provider.get()).thenReturn(sqlQueryExecution);
        when(taskExecutor.getGuid()).thenReturn(taskExecutorGuid);
        when(taskExecutor.getStartTime()).thenReturn(executorStartTime);
        when(sqlQueryExecutionDao.getByExecutionId(taskExecutorGuid)).thenReturn(sqlQueryExecution);
    }

    @Test
    public void testTaskLaunching() {
        List<TaskExecutor> ongoingExecutions = new LinkedList<>();
        SqlQueryTaskScheduler sqlQueryTaskScheduler = new SqlQueryTaskScheduler(scheduler, sqlQueryExecutionDao, sqlQueryTaskFactory, provider,
                sqlQueryTaskExecutorListener, ongoingExecutions, sqlQuery);

        sqlQueryTaskScheduler.taskLaunching(taskExecutor);

        assertThat(sqlQueryExecution.getExecutionId(), is(taskExecutorGuid));
        assertThat(sqlQueryExecution.getExecutionStart(), is(executorStartTime));
        assertThat(sqlQueryExecution.getSqlQuery(), is(sqlQuery));
        assertThat(sqlQueryExecution.getStatus(), is(ONGOING));
        assertThat(ongoingExecutions, hasSize(1));
    }

    @Test
    public void testTaskSucceeded() {
        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor);

        SqlQueryTaskScheduler sqlQueryTaskScheduler = new SqlQueryTaskScheduler(scheduler, sqlQueryExecutionDao, sqlQueryTaskFactory, provider,
                sqlQueryTaskExecutorListener, ongoingExecutions, sqlQuery);

        sqlQueryTaskScheduler.taskSucceeded(taskExecutor);

        assertThat(ongoingExecutions, hasSize(0));
    }

    @Test
    public void testTaskFailed() {
        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor);
        SqlQueryTaskScheduler sqlQueryTaskScheduler = new SqlQueryTaskScheduler(scheduler, sqlQueryExecutionDao, sqlQueryTaskFactory, provider,
                sqlQueryTaskExecutorListener, ongoingExecutions, sqlQuery);

        sqlQueryTaskScheduler.taskFailed(taskExecutor, new RuntimeException());
        assertThat(ongoingExecutions, hasSize(0));
    }

    @Test
    public void testOngoingQueryTasks() {
        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor);
        SqlQueryTaskScheduler sqlQueryTaskScheduler = new SqlQueryTaskScheduler(scheduler, sqlQueryExecutionDao, sqlQueryTaskFactory, provider,
                sqlQueryTaskExecutorListener, ongoingExecutions, sqlQuery);

        List<OngoingSqlQueryTask> tasks = sqlQueryTaskScheduler.ongoingQueryTasks();

        assertThat(tasks, hasSize(1));

        OngoingSqlQueryTask ongoingSqlQueryTask = tasks.get(0);

        assertThat(ongoingSqlQueryTask.getExecutionId(), is(taskExecutorGuid));
        assertThat(ongoingSqlQueryTask.getSqlQuery(), is(sqlQuery));
        assertThat(ongoingSqlQueryTask.getStartTime(), is(executorStartTime));
    }

    @Test
    public void testStopExecution() throws SqlQueryExecutionNotFoundException {
        TaskExecutor another = mock(TaskExecutor.class);
        when(another.getGuid()).thenReturn("");

        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor, another);

        SqlQueryTaskScheduler sqlQueryTaskScheduler = new SqlQueryTaskScheduler(scheduler, sqlQueryExecutionDao, sqlQueryTaskFactory, provider,
                sqlQueryTaskExecutorListener, ongoingExecutions, sqlQuery);

        sqlQueryTaskScheduler.stopExecution(taskExecutorGuid);

        verify(taskExecutor, times(1)).stop();
        verify(another, times(0)).stop();

        assertThat(ongoingExecutions, hasSize(2));
    }
}
