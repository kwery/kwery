package controllers.apis.integration.datasourceapicontroller;

import controllers.apis.integration.userapicontroller.PostLoginApiTest;
import controllers.util.TestUtil;
import dao.DatasourceDao;
import models.Datasource;
import org.junit.Before;

import static conf.Routes.ADD_DATASOURCE_API;
import static conf.Routes.MYSQL_DATASOURCE_CONNECTION_TEST_API;

public abstract class DatasoureApiControllerTest extends PostLoginApiTest {
    protected DatasourceDao datasourceDao;
    protected String addDatasourceApi;
    protected String mysqlDatasourceConnectionTestApi;
    protected Datasource datasource;

    @Before
    public void before() {
        super.before();
        datasourceDao = getInjector().getInstance(DatasourceDao.class);
        addDatasourceApi = getUrl(ADD_DATASOURCE_API);
        mysqlDatasourceConnectionTestApi = getUrl(MYSQL_DATASOURCE_CONNECTION_TEST_API);
        datasource = TestUtil.datasource();
    }
}
