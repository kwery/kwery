package com.kwery.tests.services.oneoffexecution;

import com.google.common.collect.ImmutableList;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.awaitility.Awaitility;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.Status.FAILURE;
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

        SqlQueryModel sqlQuery = sqlQueryDao.getById(failQueryId);
        schedulerService.schedule(sqlQuery);
        SECONDS.sleep(30);

        Awaitility.waitAtMost(30, SECONDS).until(() -> !oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs().isEmpty());

        List<SqlQueryExecutionModel> executions = getSqlQueryExecutions();

        Awaitility.waitAtMost(30, SECONDS).until(() -> !executions.isEmpty());

        assertThat(executions, hasSize(1));

        SqlQueryExecutionModel sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(sqlQueryExecution.getResult(), nullValue());
        assertThat(sqlQueryExecution.getStatus(), is(FAILURE));

        //So that the scheduler gets reaped
        Awaitility.waitAtMost(120, SECONDS).until(() -> oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs().isEmpty());

        assertThat(sqlQueryTaskSchedulerHolder.all(), emptyIterable());
        assertThat(schedulerService.ongoingQueryTasks(failQueryId), iterableWithSize(0));

        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat("Report email is not sent in case query execution failed", mail, nullValue());
    }

    private List<SqlQueryExecutionModel> getSqlQueryExecutions() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(failQueryId);
        filter.setStatuses(ImmutableList.of(FAILURE));
        return sqlQueryExecutionDao.filter(filter);
    }
}
