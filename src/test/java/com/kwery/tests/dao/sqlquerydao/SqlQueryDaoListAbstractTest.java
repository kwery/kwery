package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;

public class SqlQueryDaoListAbstractTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;

    @Before
    public void setUp() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);

        SqlQueryModel sqlQueryModel2 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel2);

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }
}
