package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoCountSqlQueriesWithDatasourceIdTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;
    private SqlQueryModel sqlQueryModel0;
    private Datasource datasource;

    @Before
    public void setUp() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() {
        assertThat(sqlQueryDao.countSqlQueriesWithDatasourceId(datasource.getId()), is(2l));
    }
}
