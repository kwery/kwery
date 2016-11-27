package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.views.ActionResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.text.MessageFormat;

import static com.kwery.conf.Routes.ADD_DATASOURCE_API;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AddDatasourceSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;

    @Before
    public void addDatasourceSuccessTestSetup() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
    }

    @Test
    public void test() throws IOException {
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_DATASOURCE_API), datasource));
        String successMessage = MessageFormat.format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, datasource.getLabel());
        assertSuccess(successResult, successMessage);

        assertThat(getInjector().getInstance(DatasourceDao.class).getByLabel(datasource.getLabel()), notNullValue());
    }
}
