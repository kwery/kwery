package com.kwery.tests.services.scheduledexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.services.scheduledexecution.DependentSqlQueriesSetUp.dependentSelectQueryId;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionWithDependentsOngoingAndKilledTest extends SchedulerServiceScheduledExecutionOngoingAndKilledTest {
    @Before
    public void setUpSchedulerServiceScheduledExecutionWithDependentsOngoingAndKilledTest() {
        new DependentSqlQueriesSetUp().setUp();
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException, SqlQueryExecutionNotFoundException {
        super.test();

        SqlQueryExecutionSearchFilter dependentExecutionFilter = new SqlQueryExecutionSearchFilter();
        dependentExecutionFilter.setSqlQueryId(dependentSelectQueryId);
        assertThat("Dependent SQL query has not been executed", sqlQueryExecutionDao.filter(dependentExecutionFilter), hasSize(0));
    }
}
