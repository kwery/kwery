package com.kwery.tests.dao.datasourcedao;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceTable;
import static com.kwery.tests.util.TestUtil.datasource;

public class DatasourceDaoDeleteSuccessTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;
    private Datasource datasource;

    @Before
    public void setUpDatasourceDaoDeleteTest() {
        datasource = datasource();
        datasourceDbSetup(datasource);
        datasourceDao = getInstance(DatasourceDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        datasourceDao.delete(datasource.getId());
        new DbTableAsserterBuilder(Datasource.TABLE, datasourceTable(null)).build().assertTable();
    }
}
