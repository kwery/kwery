package com.kwery.tests.services.oneoffexecution;

import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.FAILURE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SchedulerServiceOneOffExecutionFailedTest extends SchedulerServiceOneOffExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, SQLException, IOException {
        long start = System.currentTimeMillis();

        SqlQuery sqlQuery = sqlQueryDao.getById(failQueryId);
        schedulerService.schedule(sqlQuery);
        SECONDS.sleep(30);

        assertThat(oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs(), iterableWithSize(1));

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(failQueryId);

        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);

        assertThat(executions, hasSize(1));

        SqlQueryExecution sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(sqlQueryExecution.getResult(), nullValue());
        assertThat(sqlQueryExecution.getStatus(), is(FAILURE));

        //So that the scheduler gets reaped
        SECONDS.sleep(120);

        assertThat(sqlQueryTaskSchedulerHolder.all(), emptyIterable());
        assertThat(oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs(), iterableWithSize(0));
        assertThat(schedulerService.ongoingQueryTasks(failQueryId), iterableWithSize(0));
    }
}
