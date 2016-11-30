package com.kwery.tests.services.scheduledexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionSuccessTest extends SchedulerServiceScheduledExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        SqlQueryModel sqlQuery = sqlQueryDao.getById(successQueryId);

        schedulerService.schedule(sqlQuery);

        Awaitility.waitAtMost(3, MINUTES).until(() -> successfulExecutions().size() >= 2);

        String expectedResult = new ObjectMapper().writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("User"),
                        ImmutableList.of("root")
                )
        );

        for (SqlQueryExecutionModel execution : successfulExecutions()) {
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getSqlQuery().getId(), is(sqlQuery.getId()));
            assertThat(execution.getStatus(), is(SUCCESS));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getExecutionId().length(), greaterThan(0));
            assertThat(execution.getResult(), is(expectedResult));
        }


        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat(mail, notNullValue());
    }

    public List<SqlQueryExecutionModel> successfulExecutions() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(successQueryId);
        filter.setStatuses(ImmutableList.of(SUCCESS));
        return sqlQueryExecutionDao.filter(filter);
    }
}
