package com.kwery.tests.services.scheduledexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kwery.models.SqlQueryModel;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.OngoingSqlQueryTask;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryTaskScheduler;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.KILLED;
import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionOngoingAndKilledTest extends SchedulerServiceScheduledExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, JsonProcessingException, SqlQueryExecutionNotFoundException {
        long now = System.currentTimeMillis();

        SqlQueryModel sqlQuery = sqlQueryDao.getById(sleepQueryId);

        schedulerService.schedule(sqlQuery);

        Awaitility.waitAtMost(2, MINUTES).until(() -> schedulerService.ongoingQueryTasks(sleepQueryId).size() >= 2);

        List<OngoingSqlQueryTask> ongoing = schedulerService.ongoingQueryTasks(sleepQueryId);
        int ongoingTasksSize = ongoing.size();
        assertThat(ongoingTasksSize, greaterThanOrEqualTo(2));

        List<String> ongoingExecutionIds = new LinkedList<>();

        for (OngoingSqlQueryTask ongoingSqlQueryTask : ongoing) {
            SqlQueryExecution execution = sqlQueryExecutionDao.getByExecutionId(ongoingSqlQueryTask.getExecutionId());
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getSqlQuery().getId(), is(sqlQuery.getId()));
            assertThat(execution.getStatus(), is(ONGOING));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), nullValue());
            assertThat(execution.getResult(), nullValue());
            ongoingExecutionIds.add(ongoingSqlQueryTask.getExecutionId());
        }

        for (OngoingSqlQueryTask ongoingSqlQueryTask : ongoing) {
            schedulerService.stopExecution(sqlQuery.getId(), ongoingSqlQueryTask.getExecutionId());
        }

        MINUTES.sleep(2);

        ongoing = schedulerService.ongoingQueryTasks(sqlQuery.getId());
        for (OngoingSqlQueryTask ongoingSqlQueryTask : ongoing) {
            assertThat(ongoingExecutionIds, not(hasItem(ongoingSqlQueryTask.getExecutionId())));
        }

        for (String ongoingExecutionId : ongoingExecutionIds) {
            SqlQueryExecution execution = sqlQueryExecutionDao.getByExecutionId(ongoingExecutionId);
            assertThat(execution.getStatus(), is(KILLED));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getResult(), nullValue());
        }

        schedulerService.shutdownSchedulers();

        boolean stopped = true;

        for (SqlQueryTaskScheduler sqlQueryTaskScheduler : sqlQueryTaskSchedulerHolder.get(sqlQuery.getId())) {
            stopped = stopped && sqlQueryTaskScheduler.hasSchedulerStopped();
        }

        assertThat(stopped, is(true));

        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat(mail, nullValue());
    }
}
