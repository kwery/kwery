package com.kwery.tests.dao.sqlqueryexecutiondao;

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

import java.util.UUID;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.*;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class SqlQueryExecutionDaoCountTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    @Before
    public void setUpSqlQueryExecutionDaoCountTest () {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "testQuery0", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(3, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, 1) //Fri Sep 30 19:51:47 IST 2016
                                .values(4, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .build()
                )
        );
        dbSetup.launch();
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() {
        //assertThat(sqlQueryExecutionDao.count(), is(4l));
    }
}
