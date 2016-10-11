package dao.sqlquerydao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.SqlQueryDao;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import static com.ninja_squad.dbsetup.Operations.insertInto;
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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoCountSqlQueriesWithDatasourceIdTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;

    @Before
    public void setUp() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1)
                                .values(2, "* * * * *", "testQuery1", "select * from foo", 1)
                                .build()
                )
        ).launch();

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() {
        assertThat(sqlQueryDao.countSqlQueriesWithDatasourceId(1), is(2l));
    }
}
