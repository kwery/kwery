package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
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
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoLastSuccessfulExecutionTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    private SqlQueryModel sqlQueryModel0;
    private SqlQueryModel sqlQueryModel1;

    @Before
    public void setUpSqlQueryExecutionDaoTestFilter() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);

        sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);

        SqlQueryModel sqlQueryModel2 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel2);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_ERROR, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, "result", SUCCESS, sqlQueryModel1.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(3, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(5, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(7, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, sqlQueryModel0.getId())
                                .values(9, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(10, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, sqlQueryModel2.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .build()
                )
        );
        dbSetup.launch();


        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() {
        List<SqlQueryExecutionModel> executions = sqlQueryExecutionDao.lastSuccessfulExecution(ImmutableList.of(sqlQueryModel0.getId(), sqlQueryModel1.getId()));
        assertThat(executions, hasSize(2));

        List<Integer> queryIds = new ArrayList<>(2);
        for (SqlQueryExecutionModel execution : executions) {
            queryIds.add(execution.getId());
        }

        assertThat(queryIds, containsInAnyOrder(9, 2));
    }
}
