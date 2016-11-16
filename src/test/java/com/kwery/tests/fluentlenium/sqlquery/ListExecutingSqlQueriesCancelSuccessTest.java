package com.kwery.tests.fluentlenium.sqlquery;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.User;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.MySqlDocker;

import java.util.concurrent.TimeUnit;

import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.KILLED_M;
import static com.kwery.tests.util.Messages.KILL_M;
import static com.kwery.tests.util.TestUtil.sleepSqlQuery;

public class ListExecutingSqlQueriesCancelSuccessTest extends RepoDashFluentLeniumTest {
    protected MySqlDocker mySqlDocker = new MySqlDocker();
    protected SchedulerService schedulerService;
    protected ListExecutingSqlQueriesPage page;

    @Before
    public void setUpListExecutingSqlQueriesCancelSuccessTest () throws InterruptedException {
        mySqlDocker.start();

        UserTableUtil userTableUtil = new UserTableUtil();
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        userTableUtil.insertOperation()
                )
        );
        dbSetup.launch();

        Datasource datasource = mySqlDocker.datasource();
        getInjector().getInstance(DatasourceDao.class).save(datasource);

        SqlQuery sqlQuery = sleepSqlQuery(datasource);
        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = getInjector().getInstance(SchedulerService.class);
        schedulerService.schedule(sqlQuery);

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }

        User user = userTableUtil.firstRow();
        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user.getUsername());

        TimeUnit.SECONDS.sleep(70);

        page = createPage(ListExecutingSqlQueriesPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Could not render list ongoing SQL queries page");
        }
    }

    @Test
    public void test() throws InterruptedException {
        FluentWebElement killButton = $("#executingSqlQueriesTable tr td button", 0);
        assertThat(killButton.getText().toLowerCase(), is(KILL_M.toLowerCase()));
        killButton.click();
        TimeUnit.SECONDS.sleep(30);
        assertThat($("#executingSqlQueriesTable tr td button", 0).getText().toLowerCase(), is(KILLED_M.toLowerCase()));
    }

    @After
    public void tearDownListExecutingSqlQueriesCancelSuccessTest() {
        mySqlDocker.tearDown();
    }
}
