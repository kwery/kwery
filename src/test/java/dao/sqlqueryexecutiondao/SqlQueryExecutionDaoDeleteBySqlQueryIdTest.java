package dao.sqlqueryexecutiondao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.SqlQueryExecutionDao;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import java.io.IOException;
import java.sql.SQLException;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static fluentlenium.utils.DbUtil.assertDbState;
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
import static models.SqlQueryExecution.Status.SUCCESS;

public class SqlQueryExecutionDaoDeleteBySqlQueryIdTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    @Before
    public void setUpSqlQueryExecutionDaoDeleteBySqlQueryIdTest() {
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
                                .values(2, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        sqlQueryExecutionDao.deleteBySqlQueryId(1);
        assertDbState(SqlQueryExecution.TABLE, "sqlQueryExecutionDaoDeleteBySqlQueryIdTest.xml");
    }
}