package com.kwery.tests.services.oneoffexecution;

import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.services.oneoffexecution.DependentSqlQueriesSetUp.dependentSelectQueryId;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SchedulerServiceOneOffExecutionWithDependentsOngoingAndKillExecutionTest extends SchedulerServiceOneOffExecutionOngoingAndKillExecutionTest {
    @Before
    public void setUpSchedulerServiceOneOffExecutionWithDependentsOngoingAndKillExecutionTest() {
        new DependentSqlQueriesSetUp().setUp();
    }

    @Test
    public void test() throws InterruptedException, SQLException, IOException, SqlQueryExecutionNotFoundException {
        super.test();

        SqlQueryExecutionSearchFilter dependentExecutionFilter = new SqlQueryExecutionSearchFilter();
        dependentExecutionFilter.setSqlQueryId(dependentSelectQueryId);
        assertThat("Dependent SQL query has not been executed", sqlQueryExecutionDao.filter(dependentExecutionFilter), hasSize(0));
    }
}
