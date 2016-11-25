package com.kwery.tests.services.scheduledexecution;

import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.codehaus.jackson.JsonProcessingException;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.FAILURE;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionFailureTest extends SchedulerServiceScheduledExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        SqlQuery sqlQuery = sqlQueryDao.getById(failQueryId);

        schedulerService.schedule(sqlQuery);
        MINUTES.sleep(3);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(failQueryId);

        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);
        assertThat(executions.size(), greaterThanOrEqualTo(2));

        for (SqlQueryExecution execution : executions) {
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getSqlQuery().getId(), is(sqlQuery.getId()));
            assertThat(execution.getStatus(), is(FAILURE));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getExecutionId().length(), greaterThan(0));
            assertThat(execution.getResult(), nullValue());
        }

        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat(mail, nullValue());
    }
}
