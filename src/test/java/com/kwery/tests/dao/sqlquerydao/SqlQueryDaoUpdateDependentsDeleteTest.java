package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoUpdateDependentsDeleteTest extends RepoDashDaoTestBase {
    protected int sqlQueryId = 1;
    protected int dependentQueryId0 = 2;
    protected int dependentQueryId1 = 3;
    protected int dependentQueryId2 = 4;

    protected SqlQueryDao sqlQueryDao;

    @Before
    public void setUpSqlQueryDaoUpdateDependentsTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(sqlQueryId, "* * * * *", "query", "select * from foo", 1)
                                .values(dependentQueryId0, "", "dependentQuery0", "select * from foo", 1)
                                .values(dependentQueryId1, "", "dependentQuery1", "select * from foo", 1)
                                .values(dependentQueryId2, "", "dependentQuery2", "select * from foo", 1)
                                .build(),
                        insertInto(SqlQuery.TABLE_QUERY_RUN_DEPENDENT)
                                .columns(SqlQuery.COLUMN_QUERY_RUN_ID_FK, SqlQuery.COLUMN_DEPENDENT_QUERY_RUN_ID_FK)
                                .values(sqlQueryId, dependentQueryId0)
                                .values(sqlQueryId, dependentQueryId1)
                                .values(sqlQueryId, dependentQueryId2)
                                .build()
                )
        ).launch();

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery sqlQuery = sqlQueryDao.getById(sqlQueryId);

        Iterator<SqlQuery> sqlQueryIterator = sqlQuery.getDependentQueries().iterator();

        while (sqlQueryIterator.hasNext()) {
            SqlQuery dependentSqlQuery = sqlQueryIterator.next();
            if (dependentSqlQuery.getId().equals(dependentQueryId2)) {
                sqlQueryIterator.remove();
            }
        }

        sqlQueryDao.update(sqlQuery);

        assertDbState(SqlQuery.TABLE_QUERY_RUN_DEPENDENT, "sqlQueryDaoUpdateDependentsUpdateTest.xml");
    }

    @Test
    public void testEmptyDependents() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery sqlQuery = sqlQueryDao.getById(sqlQueryId);
        sqlQuery.setDependentQueries(null);
        sqlQueryDao.update(sqlQuery);

        assertDbState(SqlQuery.TABLE_QUERY_RUN_DEPENDENT, "emptyQueryRunDependent.xml");
    }
}
