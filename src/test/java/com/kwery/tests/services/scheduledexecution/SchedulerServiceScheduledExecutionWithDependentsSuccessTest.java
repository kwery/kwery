package com.kwery.tests.services.scheduledexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static com.kwery.tests.services.scheduledexecution.DependentSqlQueriesSetUp.dependentSelectQueryId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionWithDependentsSuccessTest extends SchedulerServiceScheduledExecutionSuccessTest {
    public long startTime;

    @Before
    public void setUpSchedulerServiceScheduledExecutionWithDependentsSuccessTest() {
        startTime = System.currentTimeMillis();
        new DependentSqlQueriesSetUp().setUp();
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        super.test();

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(dependentSelectQueryId);

        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);
        assertThat(executions.size(), greaterThanOrEqualTo(2));

        String expectedResult = new ObjectMapper().writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("User"),
                        ImmutableList.of("root")
                )
        );

        for (SqlQueryExecution execution : executions) {
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getSqlQuery().getId(), is(dependentSelectQueryId));
            assertThat(execution.getStatus(), is(SUCCESS));
            assertThat(execution.getExecutionStart(), greaterThan(startTime));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getExecutionId().length(), greaterThan(0));
            assertThat(execution.getResult(), is(expectedResult));
        }


        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat(mail, notNullValue());
    }
}
