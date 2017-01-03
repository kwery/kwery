package com.kwery.tests.dao.datasourcedao;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;

public class DatasourceDaoDeleteFailureTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;
    protected Datasource datasource;

    @Before
    public void setUpDatasourceDaoDeleteTestFailure() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);
        datasourceDao = getInstance(DatasourceDao.class);
    }

    @Test(expected = PersistenceException.class)
    public void test() {
        datasourceDao.delete(datasource.getId());
    }
}
