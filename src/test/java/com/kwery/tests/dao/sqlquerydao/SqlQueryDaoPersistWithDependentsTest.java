package com.kwery.tests.dao.sqlquerydao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.DatasourceDao;
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
import static com.kwery.tests.util.TestUtil.queryRun;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class SqlQueryDaoPersistWithDependentsTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;
    protected SqlQueryDao sqlQueryDao;

    @Before
    public void setUpSqlQueryDaoPersistWithDependentsTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "username")
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "", "selectQuery", "select * from mysql.db", 1)
                                .values(2, "", "sleepQuery", "select sleep(100000)", 1)
                                .build()
                )
        ).launch();

        datasourceDao = getInstance(DatasourceDao.class);
        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void save() throws DatabaseUnitException, SQLException, IOException {
        Datasource datasource = datasourceDao.getById(1);

        SqlQuery q = queryRun();
        q.setDatasource(datasource);

        q.setDependentQueries(ImmutableList.of(
               sqlQueryDao.getById(1)
        ));

        sqlQueryDao.save(q);

        assertDbState("query_run_dependent", "sqlQueryDaoPersistWithDependentsSaveTest.xml");
        assertDbState("query_run", "sqlQueryDaoPersistWithDependentsSaveTest.xml");
    }

    @Test
    public void update() throws DatabaseUnitException, SQLException, IOException {
        save();
        SqlQuery sqlQuery = sqlQueryDao.getById(100);
        sqlQuery.setLabel("foo");

        assertDbState("query_run_dependent", "sqlQueryDaoPersistWithDependentsSaveTest.xml");
        assertDbState("query_run", "sqlQueryDaoPersistWithDependentsSaveTest.xml");
    }
}
