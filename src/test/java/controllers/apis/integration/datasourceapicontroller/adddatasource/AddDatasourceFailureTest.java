package controllers.apis.integration.datasourceapicontroller.adddatasource;

import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import models.Datasource;
import org.junit.Before;
import org.junit.Test;
import util.TestUtil;
import views.ActionResult;

import java.io.IOException;

import static conf.Routes.ADD_DATASOURCE_API;
import static java.text.MessageFormat.format;
import static models.Datasource.Type.MYSQL;
import static util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;

public class AddDatasourceFailureTest extends AbstractPostLoginApiTest {
    protected Datasource datasource;

    @Before
    public void addDatasourceFailureTestSetup() {
        datasource = TestUtil.datasource();
        getInjector().getInstance(DatasourceDao.class).save(datasource);
    }

    @Test
    public void test() throws IOException {
        datasource.setId(null);
        ActionResult failureResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_DATASOURCE_API), datasource));
        assertFailure(failureResult,
                format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                MYSQL_DATASOURCE_CONNECTION_FAILURE_M
        );
    }
}
