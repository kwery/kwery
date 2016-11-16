package com.kwery.tests.dao.datasourcedao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.dao.DatasourceDao;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.models.Datasource;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.RepoDashDaoTestBase;

import java.io.IOException;
import java.sql.SQLException;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;

public class DatasourceDaoDeleteSuccessTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;

    @Before
    public void setUpDatasourceDaoDeleteTest() {
            DbSetup dbSetup = new DbSetup(
                    new DataSourceDestination(DbUtil.getDatasource()),
                    insertInto(Datasource.TABLE)
                            .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                            .values(1, "testDatasource0", "password0", 3306, MYSQL.name(), "foo.com", "user0")
                            .build()
            );

            dbSetup.launch();

            datasourceDao = getInstance(DatasourceDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        datasourceDao.delete(1);
        assertDbState(Datasource.TABLE, "datasourceDaoDeleteTestSuccess.xml");
    }
}
