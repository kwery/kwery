package com.kwery.tests.fluentlenium.datasource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static junit.framework.TestCase.fail;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceDeleteUiTest extends RepoDashFluentLeniumTest {
    protected DatasourceListPage page;

    @Before
    public void setUpDeleteDatasourcePageTest() {
        UserTableUtil userTableUtil = new UserTableUtil();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation(),
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password0", 3306, MYSQL.name(), "foo.com", "user0")
                                .values(2, "testDatasource1", "password1", 3307, MYSQL.name(), "bar.com", "user1")
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 2)
                                .build()
                )
        );
        dbSetup.launch();


        UserLoginPage loginPage = createPage(UserLoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }
        loginPage.submitForm(userTableUtil.firstRow().getUsername(), userTableUtil.firstRow().getPassword());
        loginPage.waitForSuccessMessage(userTableUtil.firstRow());

        page = createPage(DatasourceListPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list datasources page");
        }
    }

    @Test
    public void testSuccess() {
        page.delete(0);
        page.waitForDeleteSuccessMessage("testDatasource0");
        List<List<String>> rows = page.rows();
        assertThat(rows, hasSize(1));
        assertThat(rows.get(0).get(0), is("testDatasource1"));
    }

    @Test
    public void testDeleteDatasourceWithSqlQuery() {
        page.delete(1);
        page.waitForDeleteFailureSqlQueryMessage();
        List<List<String>> rows = page.rows();
        assertThat(rows, hasSize(2));
        assertThat(rows.get(0).get(0), is("testDatasource0"));
        assertThat(rows.get(1).get(0), is("testDatasource1"));
    }
}

