package controllers.apis.integration.datasourceapicontroller;

import controllers.apis.integration.userapicontroller.PostLoginApiTest;
import controllers.util.TestUtil;
import dao.DatasourceDao;
import models.Datasource;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static conf.Routes.ADD_DATASOURCE_API;
import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static controllers.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public abstract class DatasoureApiControllerTest extends PostLoginApiTest {
    protected DatasourceDao datasourceDao;
    protected String addDatasourceApi;
    protected Datasource datasource;

    @Before
    public void before() {
        super.before();
        datasourceDao = getInjector().getInstance(DatasourceDao.class);
        addDatasourceApi = getUrl(ADD_DATASOURCE_API);
        datasource = TestUtil.datasource();
    }
}
