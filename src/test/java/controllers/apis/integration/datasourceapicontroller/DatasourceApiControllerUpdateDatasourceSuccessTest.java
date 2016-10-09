package controllers.apis.integration.datasourceapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.DatasourceApiController;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import ninja.Router;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.MySqlDocker;

import java.text.MessageFormat;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static util.Messages.DATASOURCE_UPDATE_SUCCESS_M;

//TODO - Validation test cases for update
public class DatasourceApiControllerUpdateDatasourceSuccessTest extends AbstractPostLoginApiTest {
    protected Datasource datasource;
    MySqlDocker mySqlDocker;

    @Before
    public void setUpDatasourceApiControllerUpdateDatasourceSuccessTest() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();

        datasource = mySqlDocker.datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, datasource.getLabel(), datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        datasource.setId(1);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(DatasourceApiController.class, "addDatasource");

        datasource.setLabel("foo");

        String response = ninjaTestBrowser.postJson(
                getUrl(url),
                datasource
        );

        Object object = Configuration.defaultConfiguration().jsonProvider().parse(response);

        assertThat(JsonPath.read(response, "$.messages.length()"), is(1));

        assertThat(object, hasJsonPath("$.messages[0]", is(MessageFormat.format(DATASOURCE_UPDATE_SUCCESS_M, MYSQL.name(), datasource.getLabel()))));
    }

    @After
    public void tearDownDatasourceApiControllerUpdateDatasourceSuccessTest() {
        mySqlDocker.tearDown();
    }
}
