package com.kwery.tests.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.RepoDashUtil;
import com.kwery.services.scheduler.*;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SqlQueryTaskTestCancel {
    @Mock
    private SqlQueryExecutionDao sqlQueryExecutionDao;
    @Mock
    private RepoDashUtil repoDashUtil;
    @Mock
    private SqlQueryModel sqlQuery;
    @Mock
    private Datasource datasource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResultSetProcessorFactory resultSetProcessorFactory;
    @Mock
    private ResultSetProcessor resultSetProcessor;
    @Mock
    private TaskExecutionContext context;
    @Mock
    private TaskExecutor taskExecutor;
    @Mock
    private PreparedStatementExecutorFactory preparedStatementExecutorFactory;
    @Mock
    private PreparedStatementExecutor preparedStatementExecutor;

    private String executionResult = "{}";

    private SqlQueryExecutionModel sqlQueryExecution = new SqlQueryExecutionModel();

    private volatile boolean cancelled = false;

    @Before
    public void setUpQueryTaskTestCancel() throws SQLException, JsonProcessingException {
        when(sqlQuery.getDatasource()).thenReturn(datasource);
        when(sqlQuery.getQuery()).thenReturn("");
        when(datasource.getLabel()).thenReturn("");
        when(repoDashUtil.connection(datasource)).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(preparedStatementExecutorFactory.create(preparedStatement)).thenReturn(preparedStatementExecutor);

        when(preparedStatementExecutor.execute()).thenAnswer(new Answer<Future<ResultSet>>() {
            @Override
            public Future<ResultSet> answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.DAYS.sleep(1);
                return mock(FutureTask.class);
            }
        });

        doNothing().when(preparedStatement).cancel();


/*        when(resultSetProcessorFactory.create(resultSet)).thenReturn(resultSetProcessor);
        when(resultSetProcessor.process()).thenReturn(executionResult);
        when(context.getTaskExecutor()).thenReturn(taskExecutor);
        when(taskExecutor.getGuid()).thenReturn("foo");
        when(sqlQueryExecutionDao.getByExecutionId("foo")).thenReturn(sqlQueryExecution);
        doNothing().when(sqlQueryExecutionDao).update(any());*/
    }

    @Test
    public void test() throws InterruptedException {
        SqlQueryTask sqlQueryTask = new SqlQueryTask(sqlQueryExecutionDao, repoDashUtil, preparedStatementExecutorFactory, resultSetProcessorFactory, sqlQuery);
        Thread taskExecutionThread = new Thread() {
            @Override
            public void run() {
                sqlQueryTask.execute(context);
            }
        };
        taskExecutionThread.start();
        TimeUnit.SECONDS.sleep(10);
        taskExecutionThread.interrupt();
        assertThat(sqlQueryExecution.getResult(), nullValue());
    }
}
