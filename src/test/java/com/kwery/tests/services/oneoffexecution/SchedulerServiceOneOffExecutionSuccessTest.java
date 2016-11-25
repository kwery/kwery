package com.kwery.tests.services.oneoffexecution;

import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.JsonToHtmlTable;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.dbunit.DatabaseUnitException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;

public class SchedulerServiceOneOffExecutionSuccessTest extends SchedulerServiceOneOffExecutionBaseTest {
    @Test
    public void test() throws InterruptedException, DatabaseUnitException, SQLException, IOException {
        long start = System.currentTimeMillis();

        SqlQuery sqlQuery = sqlQueryDao.getById(successQueryId);
        schedulerService.schedule(sqlQuery);
        SECONDS.sleep(30);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(successQueryId);

        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);

        assertThat(executions, hasSize(1));

        SqlQueryExecution sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(nullToEmpty(sqlQueryExecution.getResult()), not(equalTo("")));
        assertThat(sqlQueryExecution.getStatus(), is(SUCCESS));

        assertThat(sqlQueryTaskSchedulerHolder.all(), emptyIterable());
        assertThat(oneOffSqlQueryTaskSchedulerReaper.getSqlQueryTaskSchedulerExecutorPairs(), iterableWithSize(1));
        assertThat(schedulerService.ongoingQueryTasks(successQueryId), emptyIterable());

        Mail mail = ((PostofficeMockImpl)getInstance(Postoffice.class)).getLastSentMail();

        assertThat(mail, notNullValue());
        assertThat(mail.getTos(), containsInAnyOrder(recipientEmail));
        assertThat(mail.getBodyHtml(), is(new JsonToHtmlTable().convert(sqlQueryExecution.getResult())));
        assertThat(mail.getSubject(), endsWith(sqlQuery.getLabel()));
    }
}
