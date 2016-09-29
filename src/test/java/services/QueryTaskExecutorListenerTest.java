package services;

import dao.QueryRunExecutionDao;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.QueryRunExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import services.scheduler.QueryTaskExecutorListener;

import static models.QueryRunExecution.Status.FAILURE;
import static models.QueryRunExecution.Status.KILLED;
import static models.QueryRunExecution.Status.SUCCESS;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryTaskExecutorListenerTest {
    @Mock
    private QueryRunExecutionDao queryRunExecutionDao;
    @Mock
    private TaskExecutor taskExecutor;
    private QueryRunExecution queryRunExecution = new QueryRunExecution();
    private String taskExecutionId = "foo";

    @Before
    public void setUpQueryTaskExecutorListenerTest() {
        when(taskExecutor.getGuid()).thenReturn(taskExecutionId);
        when(queryRunExecutionDao.getByExecutionId(taskExecutionId)).thenReturn(queryRunExecution);
        doNothing().when(queryRunExecutionDao).update(queryRunExecution);
    }

    @Test
    public void testStopped() {
        long start = System.currentTimeMillis();
        when(taskExecutor.isStopped()).thenReturn(true);

        QueryTaskExecutorListener queryTaskExecutorListener = new QueryTaskExecutorListener(queryRunExecutionDao);
        queryTaskExecutorListener.executionTerminated(taskExecutor, null);

        assertThat(queryRunExecution.getStatus(), is(KILLED));
        assertThat(queryRunExecution.getExecutionEnd(), greaterThanOrEqualTo(start));
    }

    @Test
    public void testFailure() {
        long start = System.currentTimeMillis();
        when(taskExecutor.isStopped()).thenReturn(false);

        QueryTaskExecutorListener queryTaskExecutorListener = new QueryTaskExecutorListener(queryRunExecutionDao);
        queryTaskExecutorListener.executionTerminated(taskExecutor, new RuntimeException("test"));

        assertThat(queryRunExecution.getStatus(), is(FAILURE));
        assertThat(queryRunExecution.getExecutionEnd(), greaterThanOrEqualTo(start));
    }

    @Test
    public void testSuccess() {
        long start = System.currentTimeMillis();
        when(taskExecutor.isStopped()).thenReturn(false);

        QueryTaskExecutorListener queryTaskExecutorListener = new QueryTaskExecutorListener(queryRunExecutionDao);
        queryTaskExecutorListener.executionTerminated(taskExecutor, null);

        assertThat(queryRunExecution.getStatus(), is(SUCCESS));
        assertThat(queryRunExecution.getExecutionEnd(), greaterThanOrEqualTo(start));
    }
}
