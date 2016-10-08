package dao.datasourcedao;

import com.google.common.io.Resources;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.DatasourceDao;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
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

public class DatasourceDaoUpdateTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;

    @Before
    public void setUpDatasourceDaoUpdateTest () {
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
    public void test() throws Exception {
        Datasource datasource = datasourceDao.getById(1);

        datasource.setLabel("foo");
        datasource.setPort(3307);
        datasource.setUrl("bar.com");
        datasource.setUsername("newUsername");
        datasource.setPassword("newPassword");

        datasourceDao.update(datasource);

        IDatabaseConnection connection = new DatabaseConnection(DbUtil.getDatasource().getConnection());

        IDataSet databaseDataSet = connection.createDataSet();
        ITable actualTable = databaseDataSet.getTable(Datasource.TABLE);

        IDataSet expectedDataSet = new FlatXmlDataFileLoader().loadDataSet(Resources.getResource("datasourceDaoUpdateTest.xml"));
        ITable expectedTable = expectedDataSet.getTable(Datasource.TABLE);

        Assertion.assertEquals(expectedTable, actualTable);
    }
}
