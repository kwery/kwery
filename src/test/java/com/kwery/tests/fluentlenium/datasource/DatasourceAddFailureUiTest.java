package com.kwery.tests.fluentlenium.datasource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.Datasource;
import com.kwery.models.User;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.text.MessageFormat.format;
import static junit.framework.TestCase.fail;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;
import static com.kwery.tests.util.TestUtil.datasource;

public class DatasourceAddFailureUiTest extends RepoDashFluentLeniumTest {
    protected DatasourceAddPage page;
    protected Datasource datasource;

    @Before
    public void setUpAddDatasourceFailureTest() {
        datasource = datasource();

        UserTableUtil userTableUtil = new UserTableUtil(1);
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        userTableUtil.insertOperation(),
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        UserLoginPage loginPage = createPage(UserLoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);

        if (!loginPage.isRendered()) {
            fail("Could not render user login page");
        }

        User user = userTableUtil.row(0);
        loginPage.submitForm(user.getUsername(), user.getPassword());

        loginPage.waitForSuccessMessage(user);


        page = createPage(DatasourceAddPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update datasource page");
        }
    }

    @Test
    public void test() {
        page.submitForm(datasource.getUrl() + "sjdfldsjf", String.valueOf(datasource.getPort()), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        page.waitForFailureMessage();
        assertThat(
                page.errorMessages(),
                containsInAnyOrder(
                        format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                        MYSQL_DATASOURCE_CONNECTION_FAILURE_M
                )
        );
    }
}
