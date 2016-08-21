package controllers.apitest;

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

public class DatasourceControllerApiTest extends PostLoginApiTest {
    private DatasourceDao dao;

    @Before
    public void before() {
        super.before();
        dao = getInjector().getInstance(DatasourceDao.class);
    }

    @Test
    public void test() throws IOException {
        Datasource datasource = TestUtil.datasource();

        String url = getUrl(ADD_DATASOURCE_API);
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(url, datasource));
        String successMessage = MessageFormat.format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, datasource.getLabel());
        assertSuccess(successResult, successMessage);

        ActionResult failureResult = actionResult(ninjaTestBrowser.postJson(url, datasource));
        String failureMessage = MessageFormat.format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel());
        assertFailure(failureResult, failureMessage);

        assertThat(dao.getByLabel(datasource.getLabel()), notNullValue());
    }
}
