package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dao.SqlQueryExecutionDao;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import services.scheduler.PreparedStatementExecutor;
import services.scheduler.PreparedStatementExecutorFactory;
import services.scheduler.ResultSetProcessor;
import services.scheduler.ResultSetProcessorFactory;
import services.scheduler.SqlQueryTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SqlQueryTaskTestSuccess {
    @Mock
    private SqlQueryExecutionDao sqlQueryExecutionDao;
    @Mock
    private RepoDashUtil repoDashUtil;
    @Mock
    private SqlQuery sqlQuery;
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

    private SqlQueryExecution sqlQueryExecution = new SqlQueryExecution();

    @Before
    public void setUpQueryTaskTestSuccess() throws SQLException, JsonProcessingException {
        when(sqlQuery.getDatasource()).thenReturn(datasource);
        when(sqlQuery.getQuery()).thenReturn("");
        when(datasource.getLabel()).thenReturn("");
        when(repoDashUtil.connection(datasource)).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(preparedStatementExecutorFactory.create(preparedStatement)).thenReturn(preparedStatementExecutor);

        when(preparedStatementExecutor.execute()).thenAnswer(new Answer<Future<ResultSet>>() {
            @Override
            public Future<ResultSet> answer(InvocationOnMock invocation) throws Throwable {
                Future<ResultSet> future = mock(FutureTask.class);
                when(future.isDone()).thenReturn(true);
                when(future.get()).thenReturn(resultSet);
                return future;
            }
        });

        when(resultSetProcessorFactory.create(resultSet)).thenReturn(resultSetProcessor);
        when(resultSetProcessor.process()).thenReturn(executionResult);
        when(context.getTaskExecutor()).thenReturn(taskExecutor);
        when(taskExecutor.getGuid()).thenReturn("foo");
        when(sqlQueryExecutionDao.getByExecutionId("foo")).thenReturn(sqlQueryExecution);

        doNothing().when(sqlQueryExecutionDao).update(any());
    }

    @Test
    public void test() {
        SqlQueryTask sqlQueryTask = new SqlQueryTask(sqlQueryExecutionDao, repoDashUtil, preparedStatementExecutorFactory, resultSetProcessorFactory, sqlQuery);
        sqlQueryTask.execute(context);
        assertThat(sqlQueryExecution.getResult(), is(executionResult));
    }
}
