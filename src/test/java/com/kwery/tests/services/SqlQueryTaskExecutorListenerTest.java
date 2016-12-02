package com.kwery.tests.services;

import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.scheduler.SqlQueryTaskExecutorListener;
import it.sauronsoftware.cron4j.TaskExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.kwery.models.SqlQueryExecutionModel.Status.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SqlQueryTaskExecutorListenerTest {
    @Mock
    private SqlQueryExecutionDao sqlQueryExecutionDao;
    @Mock
    private TaskExecutor taskExecutor;
    private SqlQueryExecutionModel sqlQueryExecution = new SqlQueryExecutionModel();
    private String taskExecutionId = "foo";

    @Before
    public void setUpQueryTaskExecutorListenerTest() {
        when(taskExecutor.getGuid()).thenReturn(taskExecutionId);
        when(sqlQueryExecutionDao.getByExecutionId(taskExecutionId)).thenReturn(sqlQueryExecution);
        doNothing().when(sqlQueryExecutionDao).save(sqlQueryExecution);
    }

    @Test
    public void testStopped() {
        long start = System.currentTimeMillis();
        when(taskExecutor.isStopped()).thenReturn(true);

        SqlQueryTaskExecutorListener sqlQueryTaskExecutorListener = new SqlQueryTaskExecutorListener(sqlQueryExecutionDao);
        sqlQueryTaskExecutorListener.executionTerminated(taskExecutor, null);

        assertThat(sqlQueryExecution.getStatus(), is(KILLED));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThanOrEqualTo(start));
    }

    @Test
    public void testFailure() {
        long start = System.currentTimeMillis();
        when(taskExecutor.isStopped()).thenReturn(false);

        SqlQueryTaskExecutorListener sqlQueryTaskExecutorListener = new SqlQueryTaskExecutorListener(sqlQueryExecutionDao);
        sqlQueryTaskExecutorListener.executionTerminated(taskExecutor, new RuntimeException("test"));

        assertThat(sqlQueryExecution.getStatus(), is(FAILURE));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThanOrEqualTo(start));
    }

    @Test
    public void testSuccess() {
        long start = System.currentTimeMillis();
        when(taskExecutor.isStopped()).thenReturn(false);

        SqlQueryTaskExecutorListener sqlQueryTaskExecutorListener = new SqlQueryTaskExecutorListener(sqlQueryExecutionDao);
        sqlQueryTaskExecutorListener.executionTerminated(taskExecutor, null);

        assertThat(sqlQueryExecution.getStatus(), is(SUCCESS));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThanOrEqualTo(start));
    }
}
