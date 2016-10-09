package fluentlenium.datasource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.xebialabs.overcast.host.CloudHost;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.MySqlDocker;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static util.Messages.UPDATE_M;

public class UpdateDatasourcePageTest extends RepoDashFluentLeniumTest {
    protected UserTableUtil userTableUtil;
    protected UpdateDatasourcePage page;
    protected CloudHost cloudHost;
    private MySqlDocker mySqlDocker;
    protected Datasource datasource;

    @Before
    public void setUpUpdateDatasourcePageTest() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();

        datasource = mySqlDocker.datasource();

        userTableUtil = new UserTableUtil(1);
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        userTableUtil.insertOperation(),
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, datasource.getLabel(), datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .values(2, "foo", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);

        if (!loginPage.isRendered()) {
            fail("Could not render user login page");
        }

        User user = userTableUtil.row(0);
        loginPage.submitForm(user.getUsername(), user.getPassword());

        loginPage.waitForSuccessMessage(user);


        page = createPage(UpdateDatasourcePage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update datasource page");
        }
    }

    @Test
    public void test() {
        page.waitForForm("label", "testDatasource0");

        List<String> fields = page.formFields();

        assertThat(fields, hasSize(5));

        assertThat(fields.get(0), is(datasource.getUrl()));
        assertThat(fields.get(1), is(String.valueOf(datasource.getPort())));
        assertThat(fields.get(2), is(datasource.getUsername()));
        assertThat(fields.get(3), is(datasource.getPassword()));
        assertThat(fields.get(4), is(datasource.getLabel()));

        assertThat(page.actionLabel(), is(UPDATE_M.toUpperCase()));

        String newLabel = "newLabel";
        page.fillLabel(newLabel);
        page.submit();

        page.waitForSuccessMessage(newLabel);

        page.fillLabel("foo");
        page.submit();

        page.waitForFailureMessage("foo");
    }
}
