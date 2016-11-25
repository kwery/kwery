package com.kwery.tests.services.scheduledexecution;

import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.codehaus.jackson.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.services.scheduledexecution.DependentSqlQueriesSetUp.dependentSelectQueryId;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SchedulerServiceScheduledExecutionWithDependentsFailureTest extends SchedulerServiceScheduledExecutionFailureTest {
    @Before
    public void setUpSchedulerServiceScheduledExecutionWithDependentsFailureTest() {
        new DependentSqlQueriesSetUp().setUp();
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        super.test();

        SqlQueryExecutionSearchFilter dependentExecutionFilter = new SqlQueryExecutionSearchFilter();
        dependentExecutionFilter.setSqlQueryId(dependentSelectQueryId);

        assertThat("Dependent SQL query has not been executed", sqlQueryExecutionDao.filter(dependentExecutionFilter), hasSize(0));


        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();
        assertThat(mail, nullValue());
    }
}
