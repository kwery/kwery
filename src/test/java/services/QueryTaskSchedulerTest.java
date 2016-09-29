package services;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import dao.QueryRunExecutionDao;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import services.scheduler.OngoingQueryTask;
import services.scheduler.QueryTask;
import services.scheduler.QueryTaskExecutorListener;
import services.scheduler.QueryTaskFactory;
import services.scheduler.QueryTaskScheduler;

import java.util.LinkedList;
import java.util.List;

import static models.QueryRunExecution.Status.ONGOING;
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
public class QueryTaskSchedulerTest {
    @Mock
    private Scheduler scheduler;
    @Mock
    private QueryRunExecutionDao queryRunExecutionDao;
    @Mock
    private QueryTaskFactory queryTaskFactory;
    @Mock
    private Provider<QueryRunExecution> provider;
    @Mock
    private QueryTaskExecutorListener queryTaskExecutorListener;
    @Mock
    private TaskExecutor taskExecutor;
    @Mock
    private QueryTask queryTask;

    private QueryRun queryRun;
    private Datasource datasource;
    private QueryRunExecution queryRunExecution;
    private String taskExecutorGuid = "foo";
    private long executorStartTime = 1l;

    @Before
    public void setUpQueryTaskSchedulerTest() {
        queryRun = queryRun();
        datasource = datasource();
        queryRun.setDatasource(datasource);
        queryRunExecution = new QueryRunExecution();

        doNothing().when(scheduler).addSchedulerListener(any());
        when(scheduler.schedule(anyString(), any(Task.class))).thenReturn("");
        doNothing().when(scheduler).start();
        when(taskExecutor.getTask()).thenReturn(queryTask);
        when(queryTask.getQueryRun()).thenReturn(queryRun);
        when(provider.get()).thenReturn(queryRunExecution);
        when(taskExecutor.getGuid()).thenReturn(taskExecutorGuid);
        when(taskExecutor.getStartTime()).thenReturn(executorStartTime);
        when(queryRunExecutionDao.getByExecutionId(taskExecutorGuid)).thenReturn(queryRunExecution);
    }

    @Test
    public void testTaskLaunching() {
        List<TaskExecutor> ongoingExecutions = new LinkedList<>();
        QueryTaskScheduler queryTaskScheduler = new QueryTaskScheduler(scheduler, queryRunExecutionDao, queryTaskFactory, provider,
                queryTaskExecutorListener, ongoingExecutions, queryRun);

        queryTaskScheduler.taskLaunching(taskExecutor);

        assertThat(queryRunExecution.getExecutionId(), is(taskExecutorGuid));
        assertThat(queryRunExecution.getExecutionStart(), is(executorStartTime));
        assertThat(queryRunExecution.getQueryRun(), is(queryRun));
        assertThat(queryRunExecution.getStatus(), is(ONGOING));
        assertThat(ongoingExecutions, hasSize(1));
    }

    @Test
    public void testTaskSucceeded() {
        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor);

        QueryTaskScheduler queryTaskScheduler = new QueryTaskScheduler(scheduler, queryRunExecutionDao, queryTaskFactory, provider,
                queryTaskExecutorListener, ongoingExecutions, queryRun);

        queryTaskScheduler.taskSucceeded(taskExecutor);

        assertThat(ongoingExecutions, hasSize(0));
    }

    @Test
    public void testTaskFailed() {
        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor);
        QueryTaskScheduler queryTaskScheduler = new QueryTaskScheduler(scheduler, queryRunExecutionDao, queryTaskFactory, provider,
                queryTaskExecutorListener, ongoingExecutions, queryRun);

        queryTaskScheduler.taskFailed(taskExecutor, new RuntimeException());
        assertThat(ongoingExecutions, hasSize(0));
    }

    @Test
    public void testOngoingQueryTasks() {
        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor);
        QueryTaskScheduler queryTaskScheduler = new QueryTaskScheduler(scheduler, queryRunExecutionDao, queryTaskFactory, provider,
                queryTaskExecutorListener, ongoingExecutions, queryRun);

        List<OngoingQueryTask> tasks = queryTaskScheduler.ongoingQueryTasks();

        assertThat(tasks, hasSize(1));

        OngoingQueryTask ongoingQueryTask = tasks.get(0);

        assertThat(ongoingQueryTask.getExecutionId(), is(taskExecutorGuid));
        assertThat(ongoingQueryTask.getQueryRun(), is(queryRun));
        assertThat(ongoingQueryTask.getStartTime(), is(executorStartTime));
    }

    @Test
    public void testStopExecution() {
        TaskExecutor another = mock(TaskExecutor.class);
        when(another.getGuid()).thenReturn("");

        List<TaskExecutor> ongoingExecutions = Lists.newArrayList(taskExecutor, another);

        QueryTaskScheduler queryTaskScheduler = new QueryTaskScheduler(scheduler, queryRunExecutionDao, queryTaskFactory, provider,
                queryTaskExecutorListener, ongoingExecutions, queryRun);

        queryTaskScheduler.stopExecution(taskExecutorGuid);

        verify(taskExecutor, times(1)).stop();
        verify(another, times(0)).stop();

        assertThat(ongoingExecutions, hasSize(2));
    }
}
