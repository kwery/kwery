package com.kwery.tests.services.scheduledexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionSuccessTest extends SchedulerServiceScheduledExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        SqlQuery sqlQuery = sqlQueryDao.getById(successQueryId);

        schedulerService.schedule(sqlQuery);
        MINUTES.sleep(3);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(successQueryId);

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
            assertThat(execution.getSqlQuery().getId(), is(sqlQuery.getId()));
            assertThat(execution.getStatus(), is(SUCCESS));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getExecutionId().length(), greaterThan(0));
            assertThat(execution.getResult(), is(expectedResult));
        }
    }
}
