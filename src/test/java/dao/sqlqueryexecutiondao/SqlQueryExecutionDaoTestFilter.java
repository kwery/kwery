package dao.sqlqueryexecutiondao;

import com.google.common.collect.ImmutableList;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.SqlQueryExecutionDao;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SqlQueryExecutionSearchFilter;
import util.RepoDashDaoTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static models.SqlQuery.COLUMN_QUERY;
import static models.SqlQueryExecution.COLUMN_EXECUTION_END;
import static models.SqlQueryExecution.COLUMN_EXECUTION_ID;
import static models.SqlQueryExecution.COLUMN_EXECUTION_START;
import static models.SqlQueryExecution.COLUMN_QUERY_RUN_ID_FK;
import static models.SqlQueryExecution.COLUMN_RESULT;
import static models.SqlQueryExecution.COLUMN_STATUS;
import static models.SqlQueryExecution.Status.FAILURE;
import static models.SqlQueryExecution.Status.KILLED;
import static models.SqlQueryExecution.Status.ONGOING;
import static models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoTestFilter extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    @Before
    public void setUpSqlQueryExecutionDaoTestFilter() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1).build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(2, "* * * * *", "testQuery1", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecution.TABLE)
                                .columns(SqlQueryExecution.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)

                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, "result", SUCCESS, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(3, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(4, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(5, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(6, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(7, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, 1)
                                .values(8, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, 2)

                                .values(9, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(10, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(11, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(12, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(13, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(14, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(15, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, 1) //Fri Sep 30 19:51:47 IST 2016
                                .values(16, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, 2) //Fri Sep 30 19:51:47 IST 2016

                                .values(17, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, "result", SUCCESS, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(18, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, "result", SUCCESS, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(19, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(20, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(21, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(22, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(23, null, UUID.randomUUID().toString(), 1475331707680l, null, ONGOING, 1) //Sat Oct 01 19:51:47 IST 2016
                                .values(24, null, UUID.randomUUID().toString(), 1475331707680l, null, ONGOING, 2) //Sat Oct 01 19:51:47 IST 2016

                                .values(25, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, "result", SUCCESS, 1) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(26, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, "result", SUCCESS, 2) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(27, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, FAILURE, 1) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(28, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, FAILURE, 2) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(29, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, KILLED, 1) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(30, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, KILLED, 2) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(31, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, 1) //Sun Oct 02 20:02:05 IST 2016
                                .values(32, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, 2) //Sun Oct 02 20:02:05 IST 2016
                                .build()
                )
        );
        dbSetup.launch();


        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void testFilterByExecutionId() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setExecutionId("executionId");

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1));

        Datasource datasource = new Datasource();
        datasource.setId(1);
        datasource.setLabel("testDatasource");
        datasource.setPassword("password");
        datasource.setUsername("foo");
        datasource.setUrl("foo.com");
        datasource.setPort(3306);
        datasource.setType(MYSQL);

        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setQuery("select * from foo");
        sqlQuery.setId(1);
        sqlQuery.setLabel("testQuery0");
        sqlQuery.setCronExpression("* * * * *");

        SqlQueryExecution execution = new SqlQueryExecution();
        execution.setSqlQuery(sqlQuery);
        execution.setId(1);
        execution.setExecutionId("executionId");
        execution.setStatus(SUCCESS);
        execution.setExecutionStart(1475158740747l);
        execution.setExecutionEnd(1475159940797l);
        execution.setResult("result");

        SqlQueryExecution fromDb = sqlQueryExecutions.get(0);

        assertThat(fromDb, sameBeanAs(execution));

        assertThat(sqlQueryExecutionDao.count(filter), is(1l));
    }

    @Test
    public void testFilterBySqlQueryId() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(1);

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(16));

        assertThat(sqlQueryExecutionDao.count(filter), is(16l));
    }

    @Test
    public void testFilterBySqlQueryIdWithPagination() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(1);
        filter.setResultCount(2);
        filter.setPageNumber(0);

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);

        assertThat(sqlQueryExecutions, hasSize(2));
        assertThat(sqlQueryExecutions.get(0).getId(), is(1));
        assertThat(sqlQueryExecutions.get(1).getId(), is(3));

        filter.setPageNumber(1);

        sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);

        assertThat(sqlQueryExecutions, hasSize(2));
        assertThat(sqlQueryExecutions.get(0).getId(), is(5));
        assertThat(sqlQueryExecutions.get(1).getId(), is(7));

        assertThat(sqlQueryExecutionDao.count(filter), is(16l));
    }

    @Test
    public void testFilterByStatus() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(SUCCESS));
        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(8));

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        List<Integer> expectedIds = ImmutableList.of(
                1, 2, 9, 10, 17, 18, 25, 26
        );

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        assertThat(sqlQueryExecutionDao.count(filter), is(8l));
    }

    @Test
    public void testFilterByStatusWithPagination() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(SUCCESS));
        filter.setPageNumber(0);
        filter.setResultCount(1);

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1));

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        List<Integer> expectedIds = ImmutableList.of(1);

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        filter.setPageNumber(1);

        sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1));

        ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        expectedIds = ImmutableList.of(2);

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        assertThat(sqlQueryExecutionDao.count(filter), is(8l));
    }

    @Test
    public void testFilterByStatuses() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(SUCCESS, FAILURE, KILLED, ONGOING));
        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(32));

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        List<Integer> expectedIds = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32
        );

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        assertThat(sqlQueryExecutionDao.count(filter), is(32l));
    }

    @Test
    public void testFilterByExecutionStartEndTime() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setExecutionStartStart(1475158740746l);
        filter.setExecutionEndEnd(1475419925131l);
        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(24)); //Ongoing ones are discarded

        List<Integer> expectedIds = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 17, 18, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30
        );

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        assertThat(sqlQueryExecutionDao.count(filter), is(24l));
    }

    @Test
    public void testFilterByExecutionStartEndTimeWithPagination() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setExecutionStartStart(1475158740746l);
        filter.setExecutionEndEnd(1475419925131l);
        filter.setPageNumber(0);
        filter.setResultCount(1);

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1)); //Ongoing ones are discarded

        List<Integer> expectedIds = ImmutableList.of(1);

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        assertThat(sqlQueryExecutionDao.count(filter), is(24l));
    }
}
