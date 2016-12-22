package com.kwery.tests.services.oneoffexecution;

import com.google.common.collect.ImmutableList;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.JsonToHtmlTableConvertor;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.awaitility.Awaitility;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.services.oneoffexecution.DependentSqlQueriesSetUp.dependentSelectQueryId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;


public class SchedulerServiceOneOffExecutionWithDependentsSuccessTest extends SchedulerServiceOneOffExecutionBaseTest {
    @Before
    public void setUpSchedulerServiceOneOffExecutionWithDependentsSuccessTest() {
        new DependentSqlQueriesSetUp().setUp();
    }

    @Test
    public void test() throws InterruptedException, IOException, DatabaseUnitException, SQLException {
        long start = System.currentTimeMillis();

        SqlQueryModel sqlQuery = sqlQueryDao.getById(successQueryId);
        schedulerService.schedule(sqlQuery);

        Awaitility.waitAtMost(60, SECONDS).until(() -> !getSqlQueryExecutions(successQueryId).isEmpty());

        List<SqlQueryExecutionModel> executions = getSqlQueryExecutions(successQueryId);

        assertThat(executions, hasSize(1));

        SqlQueryExecutionModel sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(nullToEmpty(sqlQueryExecution.getResult()), not(equalTo("")));
        assertThat(sqlQueryExecution.getStatus(), is(SUCCESS));

        assertThat(sqlQueryTaskSchedulerHolder.all(), emptyIterable());
        assertThat("Reaper holds the scheduled task as well as the dependent one",
                oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs(), iterableWithSize(2));
        assertThat(schedulerService.ongoingQueryTasks(successQueryId), emptyIterable());

        Awaitility.waitAtMost(60, SECONDS).until(() -> !getSqlQueryExecutions(dependentSelectQueryId).isEmpty());

        SqlQueryExecutionModel execution = getSqlQueryExecutions(dependentSelectQueryId).get(0);

        assertThat(execution.getStatus(), is(SUCCESS));
        assertThat(execution.getExecutionStart(), greaterThan(start));
        assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
        assertThat(execution.getExecutionId(), notNullValue());
        assertThat(execution.getResult(), notNullValue());

        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();

        assertThat(mail, notNullValue());
        assertThat(mail.getTos(), containsInAnyOrder(recipientEmail));
        assertThat(mail.getBodyHtml(), is(new JsonToHtmlTableConvertor().convert(execution.getResult())));
        assertThat(mail.getSubject(), endsWith(sqlQueryDao.getById(dependentSelectQueryId).getLabel()));
    }

    private List<SqlQueryExecutionModel> getSqlQueryExecutions(int sqlQueryId) {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryId);
        filter.setStatuses(ImmutableList.of(SUCCESS));
        return sqlQueryExecutionDao.filter(filter);
    }
}
