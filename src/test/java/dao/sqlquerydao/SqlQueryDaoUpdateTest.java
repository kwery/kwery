package dao.sqlquerydao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.SQLException;

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

public class SqlQueryDaoUpdateTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;

    @Before
    public void setUpSqlQueryDaoUpdateTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .values(2, "testDatasource1", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1)
                                .values(2, "* * * * *", "testQuery1", "select * from foo", 1).build()
                )
        ).launch();

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery fromDb = sqlQueryDao.getById(1);
        fromDb.setQuery("select * from bar");
        fromDb.setLabel("testQuery2");
        fromDb.setCronExpression("*");
        fromDb.setDatasource(getInstance(DatasourceDao.class).getById(2));
        sqlQueryDao.update(fromDb);
        DbUtil.assertDbState(SqlQuery.TABLE, "sqlQueryDaoUpdateTest.xml");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateLabelToExistingValue() {
        SqlQuery fromDb = sqlQueryDao.getById(1);
        fromDb.setLabel("testQuery1");
        sqlQueryDao.update(fromDb);
    }
}
