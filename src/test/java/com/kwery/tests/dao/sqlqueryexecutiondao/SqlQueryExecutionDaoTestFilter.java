package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.exparity.hamcrest.BeanMatchers.theSameAs;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoTestFilter extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    private SqlQueryModel sqlQueryModel0;
    private SqlQueryModel sqlQueryModel1;
    private Datasource datasource;

    @Before
    public void setUpSqlQueryExecutionDaoTestFilter() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);

        sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)

                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, "result", SUCCESS, sqlQueryModel1.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(3, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(4, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, sqlQueryModel1.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(5, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(6, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, sqlQueryModel1.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(7, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, sqlQueryModel0.getId())
                                .values(8, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, sqlQueryModel1.getId())

                                .values(9, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(10, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, sqlQueryModel1.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(11, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(12, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel1.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(13, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(14, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, sqlQueryModel1.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(15, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016
                                .values(16, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, sqlQueryModel1.getId()) //Fri Sep 30 19:51:47 IST 2016

                                .values(17, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, "result", SUCCESS, sqlQueryModel0.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(18, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, "result", SUCCESS, sqlQueryModel1.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(19, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, sqlQueryModel0.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(20, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, sqlQueryModel1.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(21, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, sqlQueryModel0.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(22, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, sqlQueryModel1.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(23, null, UUID.randomUUID().toString(), 1475331707680l, null, ONGOING, sqlQueryModel0.getId()) //Sat Oct 01 19:51:47 IST 2016
                                .values(24, null, UUID.randomUUID().toString(), 1475331707680l, null, ONGOING, sqlQueryModel1.getId()) //Sat Oct 01 19:51:47 IST 2016

                                .values(25, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, "result", SUCCESS, sqlQueryModel0.getId()) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(26, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, "result", SUCCESS, sqlQueryModel1.getId()) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(27, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, FAILURE, sqlQueryModel0.getId()) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(28, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, FAILURE, sqlQueryModel1.getId()) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(29, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, KILLED, sqlQueryModel0.getId()) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(30, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, KILLED, sqlQueryModel1.getId()) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(31, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, sqlQueryModel0.getId()) //Sun Oct 02 20:02:05 IST 2016
                                .values(32, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, sqlQueryModel1.getId()) //Sun Oct 02 20:02:05 IST 2016
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

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1));

        SqlQueryExecutionModel execution = new SqlQueryExecutionModel();
        execution.setSqlQuery(sqlQueryModel0);
        execution.setId(1);
        execution.setExecutionId("executionId");
        execution.setStatus(SUCCESS);
        execution.setExecutionStart(1475158740747l);
        execution.setExecutionEnd(1475159940797l);
        execution.setResult("result");

        SqlQueryExecutionModel fromDb = sqlQueryExecutions.get(0);

        assertThat(fromDb, theSameAs(execution));

        assertThat(sqlQueryExecutionDao.count(filter), is(1l));
    }

    @Test
    public void testFilterBySqlQueryId() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryModel0.getId());

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(16));

        assertThat(sqlQueryExecutionDao.count(filter), is(16l));
    }

    @Test
    public void testFilterBySqlQueryIdWithPagination() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryModel0.getId());
        filter.setResultCount(2);
        filter.setPageNumber(0);

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);

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
        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(8));

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
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

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1));

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        List<Integer> expectedIds = ImmutableList.of(1);

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        filter.setPageNumber(1);

        sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1));

        ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
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
        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(32));

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
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
        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(24)); //Ongoing ones are discarded

        List<Integer> expectedIds = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 17, 18, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30
        );

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
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

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);
        assertThat(sqlQueryExecutions, hasSize(1)); //Ongoing ones are discarded

        List<Integer> expectedIds = ImmutableList.of(1);

        List<Integer> ids = new ArrayList<>(sqlQueryExecutions.size());
        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
            ids.add(sqlQueryExecution.getId());
        }

        assertThat(ids, containsInAnyOrder(expectedIds.toArray(new Integer[expectedIds.size()])));

        assertThat(sqlQueryExecutionDao.count(filter), is(24l));
    }
}
