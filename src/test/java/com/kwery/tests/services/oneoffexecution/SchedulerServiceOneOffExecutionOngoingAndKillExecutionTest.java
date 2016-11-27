package com.kwery.tests.services.oneoffexecution;

import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.awaitility.Awaitility;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static com.kwery.models.SqlQueryExecution.Status.KILLED;
import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SchedulerServiceOneOffExecutionOngoingAndKillExecutionTest extends SchedulerServiceOneOffExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, SQLException, IOException, SqlQueryExecutionNotFoundException {
        long start = System.currentTimeMillis();

        SqlQuery sqlQuery = sqlQueryDao.getById(sleepQueryId);
        schedulerService.schedule(sqlQuery);

        Awaitility.waitAtMost(30, SECONDS).until(() -> !getSqlQueryExecutions().isEmpty());

        List<SqlQueryExecution> executions = getSqlQueryExecutions();

        assertThat(executions, hasSize(1));

        SqlQueryExecution sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), nullValue());
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(nullToEmpty(sqlQueryExecution.getResult()), is(""));
        assertThat(sqlQueryExecution.getStatus(), is(ONGOING));

        assertThat(sqlQueryTaskSchedulerHolder.all(), iterableWithSize(1));
        assertThat(oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs(), iterableWithSize(1));
        assertThat(schedulerService.ongoingQueryTasks(sleepQueryId), iterableWithSize(1));

        schedulerService.stopExecution(sleepQueryId, sqlQueryExecution.getExecutionId());

        SECONDS.sleep(30);

        Awaitility.waitAtMost(30, SECONDS).until(() -> oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs().size() == 1);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sleepQueryId);

        executions = sqlQueryExecutionDao.filter(filter);

        assertThat(executions, hasSize(1));

        sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThan(sqlQueryExecution.getExecutionStart()));
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(nullToEmpty(sqlQueryExecution.getResult()), is(""));
        assertThat(sqlQueryExecution.getStatus(), is(KILLED));

        //Let reaper reap
        Awaitility.waitAtMost(120, SECONDS).until(() -> oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs().isEmpty());

        assertThat(sqlQueryTaskSchedulerHolder.all(), emptyIterable());
        assertThat(oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs(), emptyIterable());
        assertThat(schedulerService.ongoingQueryTasks(sleepQueryId), emptyIterable());

        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat("Report email is not sent in case query is killed", mail, nullValue());
    }

    private List<SqlQueryExecution> getSqlQueryExecutions() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sleepQueryId);
        return sqlQueryExecutionDao.filter(filter);
    }
}
